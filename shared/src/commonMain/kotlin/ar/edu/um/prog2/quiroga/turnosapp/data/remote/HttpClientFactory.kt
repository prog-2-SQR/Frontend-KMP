package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Arma el cliente HTTP de la app: URL base, JSON y timeouts. Los tests le pasan un motor falso (MockEngine). */
object HttpClientFactory {

    private const val TIMEOUT_MILLIS = 10_000L

    // ignoreUnknownKeys: si el backend agrega campos nuevos, la app no se rompe.
    val json = Json { ignoreUnknownKeys = true }

    fun create(baseUrl: String = ApiConfig.TURNOS_BASE_URL): HttpClient = HttpClient { configure(baseUrl) }

    fun create(engine: HttpClientEngine, baseUrl: String = ApiConfig.TURNOS_BASE_URL): HttpClient =
        HttpClient(engine) { configure(baseUrl) }

    private fun HttpClientConfig<*>.configure(baseUrl: String) {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT_MILLIS
            requestTimeoutMillis = TIMEOUT_MILLIS
        }
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }
    }
}
