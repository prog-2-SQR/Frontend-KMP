package ar.edu.um.prog2.quiroga.turnosapp.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** Lo que el usuario pide reservar. Los nombres son opcionales: si faltan, Turnos usa los de la cuenta. */
data class NewReservation(
    val professionalId: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val patientFirstName: String? = null,
    val patientLastName: String? = null,
) {
    companion object {
        /** La cátedra exige nombre y apellido del paciente de 2 a 100 caracteres (anexo §10). */
        val PATIENT_NAME_LENGTH = 2..100
    }
}

/** Un proceso de reserva tal como lo informa Turnos (GET/POST /api/reservations). */
data class Reservation(
    val id: String,
    val status: ReservationStatus,
    val professionalId: Long,
    val date: LocalDate,
    val startTime: LocalTime,
    val phoneRequested: Boolean,
    /** El texto con el que la cátedra pide el teléfono (anexo §15.4). */
    val phoneRequestMessage: String? = null,
    /** Por qué rechazó el último teléfono (anexo §15.7): se muestra al pedir otro. */
    val phoneRejectionMessage: String? = null,
) {
    /** En curso: todavía puede terminar de cualquier forma. La app sigue consultando su estado. */
    val isPending: Boolean get() = status.isPending

    /** Solo un turno confirmado se puede cancelar (enunciado §5 punto 14). */
    val isCancellable: Boolean get() = status == ReservationStatus.CONFIRMED
}

/** Una página de reservas propias. page empieza en 0, como en el backend. */
data class ReservationPage(
    val reservations: List<Reservation>,
    val page: Int,
    val totalPages: Int,
) {
    val hasPrevious: Boolean get() = page > 0
    val hasNext: Boolean get() = page + 1 < totalPages

    companion object {
        val EMPTY = ReservationPage(reservations = emptyList(), page = 0, totalPages = 0)
    }
}

/** Estados del proceso en Turnos, con el texto que ve el usuario. UNKNOWN: un estado nuevo que la app no conoce. */
enum class ReservationStatus(val label: String, val isPending: Boolean = false) {
    REQUESTED("Pedido enviado", isPending = true),
    HELD("Turno retenido", isPending = true),
    WAITING_FOR_PHONE("Reservado: falta confirmar tu teléfono", isPending = true),
    PHONE_SUBMITTED("Teléfono enviado, esperando la confirmación", isPending = true),
    CONFIRMED("Turno confirmado"),
    FAILED("No se pudo reservar"),
    EXPIRED("La reserva venció"),
    INVALID("La reserva no es válida"),
    CANCELLED("Reserva cancelada"),
    UNKNOWN("Estado desconocido");

    companion object {
        fun from(name: String): ReservationStatus = entries.find { it.name == name } ?: UNKNOWN
    }
}
