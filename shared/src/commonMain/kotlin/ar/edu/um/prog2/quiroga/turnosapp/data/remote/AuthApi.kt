package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.LoginRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.RegisterRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.TokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/** Llamadas HTTP de cuentas al backend de turnos. Solo habla HTTP: qué mensaje ver lo decide el repositorio. */
class AuthApi(private val client: HttpClient) {

    suspend fun register(request: RegisterRequestDto): ApiResult<Unit> =
        client.post("/api/register") { setBody(request) }.toApiResult { }

    suspend fun login(request: LoginRequestDto): ApiResult<TokenResponseDto> =
        client.post("/api/authenticate") { setBody(request) }.toApiResult { it.body<TokenResponseDto>() }
}
