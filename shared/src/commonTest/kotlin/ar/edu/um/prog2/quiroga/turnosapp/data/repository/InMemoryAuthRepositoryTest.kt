package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryAuthRepositoryTest {

    private val repository = InMemoryAuthRepository()

    @Test
    fun aRegisteredUserCanLogInIgnoringCase() = runTest {
        repository.register(Registration(login = "Juan.Perez", password = "una-clave"))

        val session = repository.login("JUAN.PEREZ", "una-clave", rememberMe = false).getOrThrow()

        assertEquals("juan.perez", session.login)
        assertTrue(session.token.isNotBlank())
    }

    @Test
    fun loginAndEmailCannotBeRepeated() = runTest {
        repository.register(Registration(login = "juan", password = "una-clave", email = "juan@example.com"))

        val sameLogin = repository.register(Registration(login = "JUAN", password = "otra-clave"))
        val sameEmail = repository.register(Registration(login = "otro", password = "otra-clave", email = "JUAN@example.com"))

        assertEquals("El usuario ya está en uso", sameLogin.exceptionOrNull()?.message)
        assertEquals("El email ya está en uso", sameEmail.exceptionOrNull()?.message)
    }

    @Test
    fun wrongPasswordAndUnknownUserGetTheSameError() = runTest {
        repository.register(Registration(login = "juan", password = "una-clave"))

        val wrongPassword = repository.login("juan", "mala", rememberMe = false)
        val unknownUser = repository.login("nadie", "mala", rememberMe = false)

        assertEquals("Usuario o contraseña incorrectos", wrongPassword.exceptionOrNull()?.message)
        assertEquals(wrongPassword.exceptionOrNull()?.message, unknownUser.exceptionOrNull()?.message)
    }
}
