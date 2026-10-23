package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import kotlinx.coroutines.CompletableDeferred

/** Repositorio de prueba: devuelve lo que el test le indica y recuerda con qué lo llamaron. */
class FakeAuthRepository(
    var loginResult: Result<AuthSession> = Result.success(AuthSession(login = "juan", token = "token")),
    var registerResult: Result<Unit> = Result.success(Unit),
) : AuthRepository {

    var lastRegistration: Registration? = null
    var lastLogin: Triple<String, String, Boolean>? = null
    var registerCalls = 0

    // Si no es null, el login espera a que el test lo complete (para ver el estado "cargando").
    var pendingLogin: CompletableDeferred<Unit>? = null

    override suspend fun register(registration: Registration): Result<Unit> {
        registerCalls++
        lastRegistration = registration
        return registerResult
    }

    override suspend fun login(username: String, password: String, rememberMe: Boolean): Result<AuthSession> {
        lastLogin = Triple(username, password, rememberMe)
        pendingLogin?.await()
        return loginResult
    }
}
