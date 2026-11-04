package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AppointmentApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.HttpClientFactory
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.domain.validation.PhoneRules
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Turnos lo simula MockEngine: responde lo que el test indica y guarda el request para revisarlo. */
class KtorAppointmentRepositoryTest {

    private var lastRequest: HttpRequestData? = null

    private fun repository(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): KtorAppointmentRepository {
        val engine = MockEngine { request ->
            lastRequest = request
            handler(request)
        }
        return KtorAppointmentRepository(AppointmentApi(HttpClientFactory.create(engine, baseUrl = "http://turnos.test")), token = "eyJ.token.firma")
    }

    private fun MockRequestHandleScope.json(status: HttpStatusCode, body: String) =
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun MockRequestHandleScope.problem(status: HttpStatusCode, code: String) =
        json(status, """{"status":${status.value},"code":"$code","detail":"texto interno"}""")

    private val monday = LocalDate(2026, 11, 2)
    private val nine = LocalTime(9, 0)
    private val newReservation = NewReservation(professionalId = 201, date = monday, startTime = nine)

    private val waitingForPhone = """
        {"id": "7b6f2c1e-0000-4000-8000-000000000001", "status": "WAITING_FOR_PHONE", "professionalId": 201,
         "date": "2026-11-02", "startTime": "09:00:00", "expiresAt": "2026-11-02T12:05:00Z", "phoneRequested": false}"""

    @Test
    fun availabilitySendsProfessionalDateAndToken() = runTest {
        val repository = repository { json(HttpStatusCode.OK, """{"professionalId": 201, "date": "2026-11-02", "slots": []}""") }

        repository.availability(201, monday)

        val url = lastRequest!!.url
        assertEquals("/api/availability", url.encodedPath)
        assertEquals("201", url.parameters["professionalId"])
        assertEquals("2026-11-02", url.parameters["date"])
        assertEquals("Bearer eyJ.token.firma", lastRequest!!.headers[HttpHeaders.Authorization])
    }

    @Test
    fun availabilityReturnsTheFreeSlots() = runTest {
        val repository = repository {
            json(HttpStatusCode.OK, """{"professionalId": 201, "date": "2026-11-02", "slots": [
                {"startTime": "09:00:00", "endTime": "09:30:00"}, {"startTime": "09:30:00", "endTime": "10:00:00"}]}""")
        }

        val slots = repository.availability(201, monday).getOrThrow()

        assertEquals(listOf(LocalTime(9, 0), LocalTime(9, 30)), slots.map { it.startTime })
        assertEquals(LocalTime(10, 0), slots.last().endTime)
    }

    @Test
    fun reserveSendsTheSlotAndReturnsTheProcess() = runTest {
        val repository = repository { json(HttpStatusCode.Created, waitingForPhone) }

        val reservation = repository.reserve(newReservation).getOrThrow()

        assertEquals(HttpMethod.Post, lastRequest!!.method)
        assertEquals("/api/reservations", lastRequest!!.url.encodedPath)
        val body = lastRequest!!.body.toByteArray().decodeToString()
        assertTrue(body.contains("\"professionalId\":201"))
        assertTrue(body.contains("\"date\":\"2026-11-02\""))
        assertTrue(body.contains("\"startTime\":\"09:00\""))
        assertFalse(body.contains("patientFirstName"), "sin nombres, Turnos usa los de la cuenta")
        assertEquals(ReservationStatus.WAITING_FOR_PHONE, reservation.status)
        assertEquals(LocalTime(9, 0), reservation.startTime)
        assertFalse(reservation.phoneRequested)
    }

    @Test
    fun reserveSendsThePatientNamesWhenGiven() = runTest {
        val repository = repository { json(HttpStatusCode.Created, waitingForPhone) }

        repository.reserve(newReservation.copy(patientFirstName = "Ana", patientLastName = "Torres"))

        val body = lastRequest!!.body.toByteArray().decodeToString()
        assertTrue(body.contains("\"patientFirstName\":\"Ana\""))
        assertTrue(body.contains("\"patientLastName\":\"Torres\""))
    }

    @Test
    fun anUnknownStatusDoesNotBreakTheApp() = runTest {
        val repository = repository { json(HttpStatusCode.Created, waitingForPhone.replace("WAITING_FOR_PHONE", "NUEVO_ESTADO")) }

        val reservation = repository.reserve(newReservation).getOrThrow()

        assertEquals(ReservationStatus.UNKNOWN, reservation.status)
    }

    @Test
    fun aTakenSlotAsksToChooseAnother() = runTest {
        for (code in listOf("SLOT_NOT_AVAILABLE", "SLOT_ALREADY_HELD", "SLOT_ALREADY_RESERVED", "HOLD_EXPIRED")) {
            val repository = repository { problem(HttpStatusCode.Conflict, code) }

            val result = repository.reserve(newReservation)

            assertEquals(KtorAppointmentRepository.SLOT_TAKEN, result.exceptionOrNull()?.message, code)
        }
    }

    @Test
    fun aPendingReservationIsExplained() = runTest {
        val repository = repository { problem(HttpStatusCode.Conflict, "RESERVATION_ALREADY_PENDING") }

        val result = repository.reserve(newReservation)

        assertEquals(KtorAppointmentRepository.ALREADY_PENDING, result.exceptionOrNull()?.message)
    }

    @Test
    fun missingNamesAreFlaggedSoTheAppCanAskForThem() = runTest {
        val repository = repository { problem(HttpStatusCode.BadRequest, "PATIENT_NAME_REQUIRED") }

        val error = repository.reserve(newReservation).exceptionOrNull() as AppointmentException

        assertTrue(error.needsPatientName)
        assertEquals(KtorAppointmentRepository.PATIENT_NAME_REQUIRED, error.message)
    }

    @Test
    fun otherErrorsDoNotAskForNames() = runTest {
        val repository = repository { problem(HttpStatusCode.BadRequest, "PROFESSIONAL_DISABLED") }

        val error = repository.availability(201, monday).exceptionOrNull() as AppointmentException

        assertFalse(error.needsPatientName)
        assertEquals(KtorAppointmentRepository.PROFESSIONAL_DISABLED, error.message)
    }

    @Test
    fun expiredSessionAsksToLogInAgain() = runTest {
        val repository = repository { respond("", HttpStatusCode.Unauthorized) }

        val result = repository.availability(201, monday)

        assertEquals(KtorAppointmentRepository.SESSION_EXPIRED, result.exceptionOrNull()?.message)
    }

    @Test
    fun catalogOrCatedraDownIsExplained() = runTest {
        val repository = repository { problem(HttpStatusCode.ServiceUnavailable, "CATALOG_UNAVAILABLE") }

        val result = repository.availability(201, monday)

        assertEquals(KtorAppointmentRepository.UNAVAILABLE, result.exceptionOrNull()?.message)
    }

    @Test
    fun anUnknownErrorShowsAGenericMessageWithoutInternals() = runTest {
        val repository = repository { problem(HttpStatusCode.BadGateway, "CATEDRA_REJECTED") }

        val result = repository.reserve(newReservation)

        assertEquals(KtorAppointmentRepository.GENERIC, result.exceptionOrNull()?.message)
    }

    @Test
    fun turnosDownIsAMessageNotACrash() = runTest {
        val repository = repository { throw IllegalStateException("Connection refused") }

        val result = repository.reserve(newReservation)

        assertEquals(KtorAppointmentRepository.NO_CONNECTION, result.exceptionOrNull()?.message)
    }

    @Test
    fun aReservationShowsThePhoneRequestAndTheRejection() = runTest {
        val repository = repository {
            json(HttpStatusCode.OK, waitingForPhone.replace("\"phoneRequested\": false",
                "\"phoneRequested\": true, \"phoneRequestMessage\": \"Ingrese un numero\", \"phoneRejectionMessage\": \"No es valido\""))
        }

        val reservation = repository.reservation("7b6f2c1e-0000-4000-8000-000000000001").getOrThrow()

        assertEquals("/api/reservations/7b6f2c1e-0000-4000-8000-000000000001", lastRequest!!.url.encodedPath)
        assertEquals("Bearer eyJ.token.firma", lastRequest!!.headers[HttpHeaders.Authorization])
        assertTrue(reservation.phoneRequested)
        assertEquals("Ingrese un numero", reservation.phoneRequestMessage)
        assertEquals("No es valido", reservation.phoneRejectionMessage)
        assertTrue(reservation.isPending)
    }

    @Test
    fun myReservationsComePagedFromTurnos() = runTest {
        val repository = repository {
            json(HttpStatusCode.OK, """{"content": [$waitingForPhone], "page": 1, "size": 20, "totalElements": 21, "totalPages": 2}""")
        }

        val page = repository.myReservations(1).getOrThrow()

        assertEquals("/api/reservations", lastRequest!!.url.encodedPath)
        assertEquals("1", lastRequest!!.url.parameters["page"])
        assertEquals("20", lastRequest!!.url.parameters["size"])
        assertEquals(1, page.reservations.size)
        assertTrue(page.hasPrevious)
        assertFalse(page.hasNext)
    }

    @Test
    fun thePhoneIsSentToItsReservation() = runTest {
        val repository = repository {
            json(HttpStatusCode.Accepted, waitingForPhone.replace("WAITING_FOR_PHONE", "PHONE_SUBMITTED"))
        }

        val reservation = repository.submitPhone("7b6f2c1e", " +54 9 261 555-1234 ").getOrThrow()

        assertEquals(HttpMethod.Post, lastRequest!!.method)
        assertEquals("/api/reservations/7b6f2c1e/phone", lastRequest!!.url.encodedPath)
        assertEquals("""{"phoneNumber":"+54 9 261 555-1234"}""", lastRequest!!.body.toByteArray().decodeToString())
        assertEquals(ReservationStatus.PHONE_SUBMITTED, reservation.status)
    }

    @Test
    fun phoneErrorsHaveTheirMessage() = runTest {
        val cases = mapOf(
            "INVALID_PHONE_NUMBER" to PhoneRules.INVALID_PHONE,
            "PHONE_NOT_REQUESTED" to KtorAppointmentRepository.PHONE_NOT_REQUESTED,
            "RESERVATION_NOT_FOUND" to KtorAppointmentRepository.RESERVATION_NOT_FOUND,
        )
        for ((code, message) in cases) {
            val repository = repository { problem(HttpStatusCode.BadRequest, code) }

            assertEquals(message, repository.submitPhone("7b6f2c1e", "123").exceptionOrNull()?.message, code)
        }
    }

    @Test
    fun cancellingSendsTheReasonToItsReservation() = runTest {
        val repository = repository { json(HttpStatusCode.OK, waitingForPhone.replace("WAITING_FOR_PHONE", "CANCELLED")) }

        val reservation = repository.cancel("7b6f2c1e").getOrThrow()

        assertEquals(HttpMethod.Post, lastRequest!!.method)
        assertEquals("/api/reservations/7b6f2c1e/cancel", lastRequest!!.url.encodedPath)
        assertEquals("Bearer eyJ.token.firma", lastRequest!!.headers[HttpHeaders.Authorization])
        assertEquals("""{"reason":"${KtorAppointmentRepository.CANCEL_REASON}"}""", lastRequest!!.body.toByteArray().decodeToString())
        assertEquals(ReservationStatus.CANCELLED, reservation.status)
    }

    @Test
    fun aReservationThatIsNotConfirmedCannotBeCancelled() = runTest {
        val repository = repository { problem(HttpStatusCode.Conflict, "RESERVATION_NOT_CANCELLABLE") }

        assertEquals(KtorAppointmentRepository.NOT_CANCELLABLE, repository.cancel("7b6f2c1e").exceptionOrNull()?.message)
    }
}
