package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AuthApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.HttpClientFactory
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** El backend lo simula MockEngine: responde lo que el test indica y guarda el request para revisarlo. */
class KtorAuthRepositoryTest {

    private var lastRequest: HttpRequestData? = null
    private var lastBody: String = ""

    private fun repository(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): KtorAuthRepository {
        val engine = MockEngine { request ->
            lastRequest = request
            lastBody = request.body.toByteArray().decodeToString()
            handler(request)
        }
        return KtorAuthRepository(AuthApi(HttpClientFactory.create(engine, baseUrl = "http://turnos.test")))
    }

    private fun MockRequestHandleScope.json(status: HttpStatusCode, body: String) =
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))

    @Test
    fun registerPostsTheJhipsterBody() = runTest {
        val repository = repository { respond("", HttpStatusCode.Created) }

        val result = repository.register(Registration(login = "juan.perez", password = "una-clave", firstName = "Juan"))

        assertTrue(result.isSuccess)
        assertEquals("http://turnos.test/api/register", lastRequest?.url.toString())
        assertTrue(lastBody.contains("\"login\":\"juan.perez\""))
        assertTrue(lastBody.contains("\"langKey\":\"es\""))
    }

    @Test
    fun registerTranslatesTheBackendCode() = runTest {
        val repository = repository {
            json(HttpStatusCode.BadRequest, """{"status":400,"code":"USERNAME_ALREADY_EXISTS","detail":"El login ya está en uso"}""")
        }

        val result = repository.register(Registration(login = "juan", password = "una-clave"))

        assertEquals("El usuario ya está en uso", result.exceptionOrNull()?.message)
    }

    @Test
    fun loginReturnsTheSessionWithTheToken() = runTest {
        val repository = repository { json(HttpStatusCode.OK, """{"id_token":"eyJ.token.firma"}""") }

        val session = repository.login("Juan.Perez", "una-clave", rememberMe = true).getOrThrow()

        assertEquals("juan.perez", session.login)
        assertEquals("eyJ.token.firma", session.token)
        assertEquals("http://turnos.test/api/authenticate", lastRequest?.url.toString())
        assertTrue(lastBody.contains("\"rememberMe\":true"))
    }

    @Test
    fun wrongCredentialsShowTheSameMessageAsTheBackend() = runTest {
        val repository = repository {
            json(HttpStatusCode.Unauthorized, """{"status":401,"code":"BAD_CREDENTIALS"}""")
        }

        val result = repository.login("juan", "mala", rememberMe = false)

        assertEquals("Usuario o contraseña incorrectos", result.exceptionOrNull()?.message)
    }

    @Test
    fun a401WithoutBodyIsAlsoBadCredentials() = runTest {
        val repository = repository { respond("", HttpStatusCode.Unauthorized) }

        assertEquals("Usuario o contraseña incorrectos", repository.login("juan", "mala", false).exceptionOrNull()?.message)
    }

    @Test
    fun anUnknownErrorShowsAGenericMessage() = runTest {
        val repository = repository { json(HttpStatusCode.InternalServerError, """{"status":500,"code":"INTERNAL_ERROR"}""") }

        assertEquals(KtorAuthRepository.GENERIC, repository.login("juan", "clave", false).exceptionOrNull()?.message)
    }

    @Test
    fun withoutConnectionTheErrorSaysSo() = runTest {
        val repository = repository { throw IllegalStateException("Connection refused") }

        assertEquals(KtorAuthRepository.NO_CONNECTION, repository.register(Registration(login = "juan", password = "clave")).exceptionOrNull()?.message)
    }
}
