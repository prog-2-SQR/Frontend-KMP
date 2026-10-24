package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.LoginRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.RegisterRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.TokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

/** Llamadas HTTP de cuentas al backend de turnos. Solo habla HTTP: qué mensaje ver lo decide el repositorio. */
class AuthApi(private val client: HttpClient) {

    suspend fun register(request: RegisterRequestDto): ApiResult<Unit> =
        client.post("/api/register") { setBody(request) }.toResult { }

    suspend fun login(request: LoginRequestDto): ApiResult<TokenResponseDto> =
        client.post("/api/authenticate") { setBody(request) }.toResult { it.body<TokenResponseDto>() }

    private suspend fun <T> HttpResponse.toResult(onSuccess: suspend (HttpResponse) -> T): ApiResult<T> =
        if (status.isSuccess()) ApiResult.Success(onSuccess(this)) else ApiResult.Failure(problemOf(this))

    // Un 401 de Spring Security puede venir sin cuerpo: se arma el problema solo con el status.
    private suspend fun problemOf(response: HttpResponse): ProblemDto {
        val text = response.bodyAsText()
        val parsed = runCatching { HttpClientFactory.json.decodeFromString<ProblemDto>(text) }.getOrNull()
        return (parsed ?: ProblemDto()).copy(status = response.status.value)
    }
}

/** Resultado de una llamada: éxito con el valor o error del backend (status + code). */
sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val problem: ProblemDto) : ApiResult<Nothing>
}
