package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.ApiResult
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AppointmentApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.AvailableSlotDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ReservationDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.StartReservationRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import kotlinx.datetime.LocalDate

/** Turnos libres y reservas contra Turnos con el token de la sesión. Pasa los DTO al modelo y los errores a mensajes. */
class KtorAppointmentRepository(
    private val api: AppointmentApi,
    private val token: String,
) : AppointmentRepository {

    override suspend fun availability(professionalId: Long, date: LocalDate): Result<List<AvailableSlot>> =
        call { api.availability(token, professionalId, date) }.map { it.slots.map(::toSlot) }

    override suspend fun reserve(reservation: NewReservation): Result<Reservation> =
        call { api.startReservation(token, toRequest(reservation)) }.map(::toReservation)

    private suspend fun <T> call(request: suspend () -> ApiResult<T>): Result<T> =
        callBackend(::messageFor, NO_CONNECTION, ::errorFor, request)

    // Se decide por el code del problem+json; un 401 o un 503 sin code (token vencido, servicio caído) también tienen mensaje.
    private fun messageFor(problem: ProblemDto): String =
        MESSAGES[problem.code] ?: STATUS_MESSAGES[problem.status] ?: GENERIC

    private fun errorFor(message: String) =
        AppointmentException(message, needsPatientName = message == PATIENT_NAME_REQUIRED)

    private fun toSlot(dto: AvailableSlotDto) = AvailableSlot(startTime = dto.startTime, endTime = dto.endTime)

    private fun toRequest(reservation: NewReservation) = StartReservationRequestDto(
        professionalId = reservation.professionalId,
        date = reservation.date,
        startTime = reservation.startTime,
        patientFirstName = reservation.patientFirstName,
        patientLastName = reservation.patientLastName,
    )

    private fun toReservation(dto: ReservationDto) = Reservation(
        id = dto.id,
        status = ReservationStatus.from(dto.status),
        professionalId = dto.professionalId,
        date = dto.date,
        startTime = dto.startTime,
        phoneRequested = dto.phoneRequested,
    )

    companion object {
        const val NO_CONNECTION = "No se pudo conectar con el servidor de turnos. Probá de nuevo."
        const val SESSION_EXPIRED = "Tu sesión venció. Volvé a iniciar sesión."
        const val SLOT_TAKEN = "Ese turno ya no está libre. Elegí otro."
        const val ALREADY_PENDING = "Ya tenés una reserva en curso. Terminala antes de pedir otra."
        const val PATIENT_NAME_REQUIRED = "Completá tu nombre y apellido para reservar."
        const val PROFESSIONAL_DISABLED = "El profesional no está tomando turnos."
        const val PROFESSIONAL_NOT_FOUND = "No encontramos al profesional."
        const val INVALID_SLOT = "Ese día u horario no es válido."
        const val UNAVAILABLE = "El sistema de turnos no responde. Probá en un rato."
        const val GENERIC = "Ocurrió un error, probá de nuevo"

        // Codes de Turnos: ReservationException, AvailabilityException y VALIDATION_ERROR del handler global.
        private val MESSAGES = mapOf(
            "SLOT_NOT_AVAILABLE" to SLOT_TAKEN,
            "SLOT_ALREADY_HELD" to SLOT_TAKEN,
            "SLOT_ALREADY_RESERVED" to SLOT_TAKEN,
            "HOLD_EXPIRED" to SLOT_TAKEN,
            "RESERVATION_ALREADY_PENDING" to ALREADY_PENDING,
            "PATIENT_NAME_REQUIRED" to PATIENT_NAME_REQUIRED,
            "PROFESSIONAL_DISABLED" to PROFESSIONAL_DISABLED,
            "PROFESSIONAL_NOT_FOUND" to PROFESSIONAL_NOT_FOUND,
            "INVALID_DATE" to INVALID_SLOT,
            "INVALID_SLOT" to INVALID_SLOT,
            "VALIDATION_ERROR" to INVALID_SLOT,
            "CATALOG_UNAUTHORIZED" to SESSION_EXPIRED,
            "CATALOG_UNAVAILABLE" to UNAVAILABLE,
            "CATEDRA_UNAVAILABLE" to UNAVAILABLE,
        )

        private val STATUS_MESSAGES = mapOf(
            401 to SESSION_EXPIRED,
            503 to UNAVAILABLE,
        )
    }
}
