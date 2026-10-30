package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.AvailabilityDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ReservationDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.StartReservationRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.datetime.LocalDate

/** Llamadas HTTP a Turnos para ver turnos libres y reservar. Llevan el JWT de la sesión, que Turnos reenvía al catálogo. */
class AppointmentApi(private val client: HttpClient) {

    suspend fun availability(token: String, professionalId: Long, date: LocalDate): ApiResult<AvailabilityDto> =
        client.get("/api/availability") {
            bearerAuth(token)
            parameter("professionalId", professionalId)
            parameter("date", date.toString())
        }.toApiResult { it.body<AvailabilityDto>() }

    suspend fun startReservation(token: String, request: StartReservationRequestDto): ApiResult<ReservationDto> =
        client.post("/api/reservations") {
            bearerAuth(token)
            setBody(request)
        }.toApiResult { it.body<ReservationDto>() }
}
