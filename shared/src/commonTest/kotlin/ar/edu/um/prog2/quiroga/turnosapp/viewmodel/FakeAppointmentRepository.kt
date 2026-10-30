package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
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
    }
}
