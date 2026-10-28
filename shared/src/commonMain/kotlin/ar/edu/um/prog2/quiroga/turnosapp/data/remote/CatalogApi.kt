package ar.edu.um.prog2.quiroga.turnosapp.data.remote

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.CategoryDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProfessionalPageDto
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/** Llamadas HTTP al catálogo. Todas llevan el JWT de la sesión: el catálogo lo valida (Backend-Catalogo #27). */
class CatalogApi(private val client: HttpClient) {

    // Los filtros vacíos no se mandan: Ktor omite los parámetros con valor null.
    suspend fun searchProfessionals(token: String, filters: ProfessionalFilters, page: Int, size: Int): ApiResult<ProfessionalPageDto> =
        client.get("/api/professionals/search") {
            bearerAuth(token)
            parameter("name", filters.name.trim().ifEmpty { null })
            parameter("categoryId", filters.categoryId)
            parameter("dayOfWeek", filters.day?.name)
            parameter("enabled", if (filters.onlyEnabled) true else null)
            parameter("page", page)
            parameter("size", size)
        }.toApiResult { it.body<ProfessionalPageDto>() }

    suspend fun categories(token: String): ApiResult<List<CategoryDto>> =
        client.get("/api/categories") { bearerAuth(token) }.toApiResult { it.body<List<CategoryDto>>() }
}
