package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.ApiResult
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import kotlinx.coroutines.CancellationException

/**
 * Hace la llamada y la convierte en Result. Un error del backend pasa a ser un error con el mensaje que elige
 * cada repositorio (messageFor); sin conexión (backend apagado, timeout), el mensaje noConnection. Así la app
 * muestra el problema en vez de cerrarse. La cancelación de la corrutina no es un error: se deja pasar.
 */
internal suspend fun <T> callBackend(
    messageFor: (ProblemDto) -> String,
    noConnection: String,
    toError: (String) -> Exception,
    request: suspend () -> ApiResult<T>,
): Result<T> =
    try {
        when (val result = request()) {
            is ApiResult.Success -> Result.success(result.value)
            is ApiResult.Failure -> Result.failure(toError(messageFor(result.problem)))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(toError(noConnection))
    }
