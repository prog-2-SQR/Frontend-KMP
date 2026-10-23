package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration

/**
 * Lo que la app necesita del backend para las cuentas (POST /api/register y POST /api/authenticate).
 * Los ViewModels dependen de esta interfaz, no de una implementación: hoy es en memoria, después Ktor.
 */
interface AuthRepository {

    suspend fun register(registration: Registration): Result<Unit>

    suspend fun login(username: String, password: String, rememberMe: Boolean): Result<AuthSession>
}

/** Error de cuenta con un mensaje para mostrar al usuario. */
class AuthException(message: String) : Exception(message)
