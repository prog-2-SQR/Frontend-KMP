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
)

/** Estados del proceso en Turnos, con el texto que ve el usuario. UNKNOWN: un estado nuevo que la app no conoce. */
enum class ReservationStatus(val label: String) {
    REQUESTED("Pedido enviado"),
    HELD("Turno retenido"),
    WAITING_FOR_PHONE("Reservado: falta confirmar tu teléfono"),
    PHONE_SUBMITTED("Teléfono enviado, esperando la confirmación"),
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
