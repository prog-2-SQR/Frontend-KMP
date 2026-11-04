package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationPage
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Turnos falso para los tests del ViewModel: devuelve lo configurado y anota cada pedido. */
class FakeAppointmentRepository : AppointmentRepository {

    var availabilityResult: Result<List<AvailableSlot>> = Result.success(listOf(NINE, NINE_THIRTY))
    var reserveResults: MutableList<Result<Reservation>> = mutableListOf()

    // Si está, la consulta de turnos espera a que el test la complete (para ver el estado "cargando").
    var gate: CompletableDeferred<Unit>? = null

    val availabilityRequests = mutableListOf<Pair<Long, LocalDate>>()
    val reservations = mutableListOf<NewReservation>()

    override suspend fun availability(professionalId: Long, date: LocalDate): Result<List<AvailableSlot>> {
        availabilityRequests += professionalId to date
        gate?.await()
        return availabilityResult
    }

    // Devuelve los resultados configurados en orden; si no hay ninguno, la reserva sale bien.
    override suspend fun reserve(reservation: NewReservation): Result<Reservation> {
        reservations += reservation
        return reserveResults.removeFirstOrNull() ?: Result.success(reservationFor(reservation))
    }

    // Detalle de una reserva: devuelve las configuradas en orden (la última se repite), para simular que cambia.
    var reservationResults: MutableList<Result<Reservation>> = mutableListOf()
    var myReservationsResult: Result<ReservationPage> = Result.success(ReservationPage.EMPTY)
    var submitPhoneResult: Result<Reservation>? = null

    val reservationRequests = mutableListOf<String>()
    val myReservationsRequests = mutableListOf<Int>()
    val submittedPhones = mutableListOf<Pair<String, String>>()

    // Si no se configura, la cancelación sale bien y devuelve la reserva cancelada.
    var cancelResult: Result<Reservation>? = null
    val cancelled = mutableListOf<String>()

    override suspend fun reservation(id: String): Result<Reservation> {
        reservationRequests += id
        return if (reservationResults.size > 1) reservationResults.removeFirst() else reservationResults.first()
    }

    override suspend fun myReservations(page: Int): Result<ReservationPage> {
        myReservationsRequests += page
        return myReservationsResult
    }

    override suspend fun submitPhone(reservationId: String, phoneNumber: String): Result<Reservation> {
        submittedPhones += reservationId to phoneNumber
        return submitPhoneResult ?: Result.success(reservationResults.last().getOrThrow().copy(status = ReservationStatus.PHONE_SUBMITTED, phoneRequested = false))
    }

    override suspend fun cancel(reservationId: String): Result<Reservation> {
        cancelled += reservationId
        return cancelResult ?: Result.success(reservationResults.last().getOrThrow().copy(status = ReservationStatus.CANCELLED))
    }

    companion object {
        val NINE = AvailableSlot(LocalTime(9, 0), LocalTime(9, 30))
        val NINE_THIRTY = AvailableSlot(LocalTime(9, 30), LocalTime(10, 0))

        fun reservationFor(reservation: NewReservation) = Reservation(
            id = "proceso-1",
            status = ReservationStatus.WAITING_FOR_PHONE,
            professionalId = reservation.professionalId,
            date = reservation.date,
            startTime = reservation.startTime,
            phoneRequested = false,
        )

        /** Una reserva del profesional 218 para el 3/11 a las 13:00, en el estado pedido. */
        fun reservation(status: ReservationStatus, phoneRequested: Boolean = false, id: String = "proceso-1", professionalId: Long = 218) =
            Reservation(
                id = id,
                status = status,
                professionalId = professionalId,
                date = LocalDate(2026, 11, 3),
                startTime = LocalTime(13, 0),
                phoneRequested = phoneRequested,
                phoneRequestMessage = if (phoneRequested) "Ingrese un numero de telefono para continuar." else null,
            )
    }
}
