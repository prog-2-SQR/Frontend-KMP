package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration

/**
 * Implementación temporal, sin red: guarda las cuentas en memoria mientras la app está abierta.
 * Imita lo que hace el backend (login y email únicos sin importar mayúsculas; el mismo error para usuario
 * inexistente o contraseña mala) para poder probar las pantallas. Se reemplaza por la de Ktor.
 */
class InMemoryAuthRepository : AuthRepository {

    private val accounts = mutableMapOf<String, Registration>()

    override suspend fun register(registration: Registration): Result<Unit> {
        val login = registration.login.lowercase()
        val email = registration.email?.lowercase()
        return when {
            login in accounts -> Result.failure(AuthException("El usuario ya está en uso"))
            email != null && accounts.values.any { it.email == email } -> Result.failure(AuthException("El email ya está en uso"))
            else -> {
                accounts[login] = registration.copy(login = login, email = email)
                Result.success(Unit)
            }
        }
    }

    override suspend fun login(username: String, password: String, rememberMe: Boolean): Result<AuthSession> {
        val account = accounts[username.lowercase()]
        return if (account != null && account.password == password) {
            Result.success(AuthSession(login = account.login, token = "token-local-${account.login}"))
        } else {
            Result.failure(AuthException("Usuario o contraseña incorrectos"))
        }
    }
}
