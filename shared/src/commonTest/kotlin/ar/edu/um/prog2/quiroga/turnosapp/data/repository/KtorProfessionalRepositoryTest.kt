package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.CatalogApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.HttpClientFactory
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Weekday
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** El catálogo lo simula MockEngine: responde lo que el test indica y guarda el request para revisarlo. */
class KtorProfessionalRepositoryTest {

    private var lastRequest: HttpRequestData? = null

    private fun repository(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): KtorProfessionalRepository {
        val engine = MockEngine { request ->
            lastRequest = request
            handler(request)
        }
        return KtorProfessionalRepository(CatalogApi(HttpClientFactory.create(engine, baseUrl = "http://catalogo.test")), token = "eyJ.token.firma")
    }

    private fun MockRequestHandleScope.json(status: HttpStatusCode, body: String) =
        respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))

    private val onePage = """
        {"content": [{"id": 201, "categoryId": 101, "firstName": "Ana", "lastName": "Torres", "enabled": true, "campoNuevo": 1}],
         "page": 0, "size": 20, "totalElements": 1, "totalPages": 1}"""

    @Test
    fun searchSendsTheFiltersAndTheSessionToken() = runTest {
        val repository = repository { json(HttpStatusCode.OK, onePage) }

        repository.search(ProfessionalFilters(name = " ana ", categoryId = 101, day = Weekday.MONDAY), page = 2)

        val url = lastRequest!!.url
        assertEquals("/api/professionals/search", url.encodedPath)
        assertEquals("ana", url.parameters["name"])
        assertEquals("101", url.parameters["categoryId"])
        assertEquals("MONDAY", url.parameters["dayOfWeek"])
        assertEquals("true", url.parameters["enabled"])
        assertEquals("2", url.parameters["page"])
        assertEquals("20", url.parameters["size"])
        assertEquals("Bearer eyJ.token.firma", lastRequest!!.headers[HttpHeaders.Authorization])
    }

    @Test
    fun emptyFiltersAreNotSent() = runTest {
        val repository = repository { json(HttpStatusCode.OK, onePage) }

        repository.search(ProfessionalFilters(name = "  ", onlyEnabled = false), page = 0)

        val parameters = lastRequest!!.url.parameters
        assertNull(parameters["name"])
        assertNull(parameters["categoryId"])
        assertNull(parameters["dayOfWeek"])
        assertNull(parameters["enabled"])
    }

    @Test
    fun searchReturnsThePageOfTheCatalog() = runTest {
        val repository = repository { json(HttpStatusCode.OK, onePage) }

        val page = repository.search(ProfessionalFilters(), page = 0).getOrThrow()

        assertEquals(1, page.totalElements)
        assertEquals("Ana Torres", page.professionals.single().fullName)
        assertFalse(page.hasNext)
    }

    @Test
    fun categoriesComeEnabledAndSortedByName() = runTest {
        val repository = repository {
            json(HttpStatusCode.OK, """[{"id": 2, "name": "Pediatría", "enabled": true},
                {"id": 1, "name": "Cardiología", "enabled": true}, {"id": 3, "name": "Vieja", "enabled": false}]""")
        }

        val categories = repository.categories().getOrThrow()

        assertEquals(listOf("Cardiología", "Pediatría"), categories.map { it.name })
        assertEquals("/api/categories", lastRequest!!.url.encodedPath)
        assertEquals("Bearer eyJ.token.firma", lastRequest!!.headers[HttpHeaders.Authorization])
    }

    @Test
    fun expiredSessionAsksToLogInAgain() = runTest {
        val repository = repository { respond("", HttpStatusCode.Unauthorized) }

        val result = repository.search(ProfessionalFilters(), page = 0)

        assertEquals(KtorProfessionalRepository.SESSION_EXPIRED, result.exceptionOrNull()?.message)
    }

    @Test
    fun invalidFiltersShowTheirMessage() = runTest {
        val repository = repository { json(HttpStatusCode.BadRequest, """{"status":400,"code":"VALIDATION_ERROR"}""") }

        val result = repository.search(ProfessionalFilters(), page = 0)

        assertEquals(KtorProfessionalRepository.INVALID_FILTERS, result.exceptionOrNull()?.message)
    }

    @Test
    fun catalogDownIsAMessageNotACrash() = runTest {
        val repository = repository { throw IllegalStateException("Connection refused") }

        val result = repository.categories()

        assertTrue(result.isFailure)
        assertEquals(KtorProfessionalRepository.NO_CONNECTION, result.exceptionOrNull()?.message)
    }
}
