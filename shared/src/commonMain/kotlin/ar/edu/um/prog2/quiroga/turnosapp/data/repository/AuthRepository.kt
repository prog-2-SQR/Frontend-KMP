package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration

/**
 * Lo que la app necesita para las cuentas: registro y login en el backend (POST /api/register y POST /api/authenticate)
 * y la sesión guardada con "Recordarme". Los ViewModels dependen de esta interfaz; los tests usan una falsa.
 */
interface AuthRepository {

    suspend fun register(registration: Registration): Result<Unit>

    /** Con rememberMe la sesión queda guardada para la próxima vez que se abra la app. */
    suspend fun login(username: String, password: String, rememberMe: Boolean): Result<AuthSession>

    /** La sesión guardada con "Recordarme", si sigue vigente. */
    fun savedSession(): AuthSession?

    /** Olvida la sesión guardada. El token no se invalida en el backend (no tiene logout): vence solo. */
    fun logout()
}

/** Error de cuenta con un mensaje para mostrar al usuario. */
class AuthException(message: String) : Exception(message)
