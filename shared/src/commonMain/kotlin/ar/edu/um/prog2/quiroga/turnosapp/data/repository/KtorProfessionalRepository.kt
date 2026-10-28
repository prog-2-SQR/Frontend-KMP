package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.data.remote.ApiResult
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.CatalogApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.CategoryDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProblemDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProfessionalDto
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto.ProfessionalPageDto
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Category
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Professional
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalPage

/** Búsqueda contra el catálogo con el token de la sesión. Pasa los DTO al modelo de la app y los errores a mensajes. */
class KtorProfessionalRepository(
    private val api: CatalogApi,
    private val token: String,
) : ProfessionalRepository {

    override suspend fun search(filters: ProfessionalFilters, page: Int): Result<ProfessionalPage> =
        call { api.searchProfessionals(token, filters, page, PAGE_SIZE) }.map(::toPage)

    // Solo las categorías habilitadas sirven para filtrar; se muestran ordenadas por nombre.
    override suspend fun categories(): Result<List<Category>> =
        call { api.categories(token) }.map { categories ->
            categories.filter(CategoryDto::enabled).map { Category(id = it.id, name = it.name) }.sortedBy(Category::name)
        }

    private suspend fun <T> call(request: suspend () -> ApiResult<T>): Result<T> =
        callBackend(::messageFor, NO_CONNECTION, ::CatalogException, request)

    private fun messageFor(problem: ProblemDto): String = when (problem.status) {
        401 -> SESSION_EXPIRED
        400 -> INVALID_FILTERS
        else -> GENERIC
    }

    private fun toPage(dto: ProfessionalPageDto) = ProfessionalPage(
        professionals = dto.content.map(::toProfessional),
        page = dto.page,
        totalPages = dto.totalPages,
        totalElements = dto.totalElements,
    )

    private fun toProfessional(dto: ProfessionalDto) = Professional(
        id = dto.id,
        categoryId = dto.categoryId,
        firstName = dto.firstName,
        lastName = dto.lastName,
        enabled = dto.enabled,
    )

    companion object {
        const val PAGE_SIZE = 20
        const val NO_CONNECTION = "No se pudo conectar con el catálogo. Probá de nuevo."
        const val SESSION_EXPIRED = "Tu sesión venció. Volvé a iniciar sesión."
        const val INVALID_FILTERS = "Revisá los filtros de la búsqueda"
        const val GENERIC = "Ocurrió un error, probá de nuevo"
    }
}
