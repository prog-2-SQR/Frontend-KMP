package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.ApiResult
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AuthApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.LoginRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.RegisterRequestDto
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import kotlinx.coroutines.CancellationException

/** Cuentas contra el backend de turnos. Traduce los codes del backend (anexo §13.1) a mensajes para el usuario. */
class KtorAuthRepository(private val api: AuthApi) : AuthRepository {

    override suspend fun register(registration: Registration): Result<Unit> = call {
        api.register(
            RegisterRequestDto(
                login = registration.login,
                password = registration.password,
                firstName = registration.firstName,
                lastName = registration.lastName,
                email = registration.email,
                langKey = registration.langKey,
            ),
        )
    }

    override suspend fun login(username: String, password: String, rememberMe: Boolean): Result<AuthSession> =
        call { api.login(LoginRequestDto(username = username, password = password, rememberMe = rememberMe)) }
            .map { token -> AuthSession(login = username.lowercase(), token = token.idToken) }

    // Sin conexión (backend apagado, timeout) también es un error para mostrar, no un cierre de la app.
    private suspend fun <T> call(request: suspend () -> ApiResult<T>): Result<T> =
        try {
            when (val result = request()) {
                is ApiResult.Success -> Result.success(result.value)
                is ApiResult.Failure -> Result.failure(AuthException(messageFor(result.problem)))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(AuthException(NO_CONNECTION))
        }

    private fun messageFor(problem: ProblemDto): String =
        MESSAGES[problem.code] ?: if (problem.status == 401) MESSAGES.getValue("BAD_CREDENTIALS") else GENERIC

    companion object {
        const val NO_CONNECTION = "No se pudo conectar con el servidor. Probá de nuevo."
        const val GENERIC = "Ocurrió un error, probá de nuevo"

        private val MESSAGES = mapOf(
            "USERNAME_ALREADY_EXISTS" to "El usuario ya está en uso",
            "EMAIL_ALREADY_EXISTS" to "El email ya está en uso",
            "INVALID_PASSWORD" to "La contraseña debe tener entre 4 y 100 caracteres",
            "VALIDATION_ERROR" to "Revisá los datos ingresados",
            "BAD_CREDENTIALS" to "Usuario o contraseña incorrectos",
        )
    }
}
