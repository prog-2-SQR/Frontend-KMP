package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

/** Resultado de una llamada: éxito con el valor o error del backend (status + code). */
sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val problem: ProblemDto) : ApiResult<Nothing>
}

/** Respuesta 2xx → éxito con lo que lea onSuccess; cualquier otra → el problema que mandó el backend. */
internal suspend fun <T> HttpResponse.toApiResult(onSuccess: suspend (HttpResponse) -> T): ApiResult<T> =
    if (status.isSuccess()) ApiResult.Success(onSuccess(this)) else ApiResult.Failure(problemOf(this))

// Un 401 de Spring Security puede venir sin cuerpo: se arma el problema solo con el status.
private suspend fun problemOf(response: HttpResponse): ProblemDto {
    val text = response.bodyAsText()
    val parsed = runCatching { HttpClientFactory.json.decodeFromString<ProblemDto>(text) }.getOrNull()
    return (parsed ?: ProblemDto()).copy(status = response.status.value)
}
