package ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

// Las fechas viajan como yyyy-MM-dd y las horas como HH:mm:ss (anexo §4); kotlinx-datetime las lee y escribe así.

/** Respuesta de GET /api/availability de Turnos. */
@Serializable
data class AvailabilityDto(
    val professionalId: Long,
    val date: LocalDate,
    val slots: List<AvailableSlotDto> = emptyList(),
)

@Serializable
data class AvailableSlotDto(
    val startTime: LocalTime,
    val endTime: LocalTime,
)

/** Body de POST /api/reservations. */
@Serializable
data class StartReservationRequestDto(
    val professionalId: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val patientFirstName: String? = null,
    val patientLastName: String? = null,
)

/** Un proceso de reserva en Turnos. El status llega como texto para que un estado nuevo no rompa la app. */
@Serializable
data class ReservationDto(
    val id: String,
    val status: String,
    val professionalId: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val phoneRequested: Boolean = false,
    val phoneRequestMessage: String? = null,
    val phoneRejectionMessage: String? = null,
)

/** Página de GET /api/reservations: {content, page, size, totalElements, totalPages}. */
@Serializable
data class ReservationPageDto(
    val content: List<ReservationDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

/** Body de POST /api/reservations/{id}/phone. */
@Serializable
data class SubmitPhoneRequestDto(
    val phoneNumber: String,
)

/** Body de POST /api/reservations/{id}/cancel. Turnos se lo pasa a la cátedra (anexo §12: hasta 500 caracteres). */
@Serializable
data class CancelReservationRequestDto(
    val reason: String,
)
