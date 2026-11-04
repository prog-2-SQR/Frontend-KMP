package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationPage
import kotlinx.datetime.LocalDate

/** Turnos libres, reservas, teléfono y cancelación en el backend de turnos. El ViewModel depende de esta interfaz; los tests usan una falsa. */
interface AppointmentRepository {

    suspend fun availability(professionalId: Long, date: LocalDate): Result<List<AvailableSlot>>

    suspend fun reserve(reservation: NewReservation): Result<Reservation>

    suspend fun reservation(id: String): Result<Reservation>

    suspend fun myReservations(page: Int): Result<ReservationPage>

    suspend fun submitPhone(reservationId: String, phoneNumber: String): Result<Reservation>

    suspend fun cancel(reservationId: String): Result<Reservation>
}

/**
 * Error al ver turnos o reservar, con un mensaje para mostrar al usuario.
 * needsPatientName: la cuenta no tiene nombre y apellido, así que la app tiene que pedirlos para reservar.
 */
class AppointmentException(message: String, val needsPatientName: Boolean = false) : Exception(message)
