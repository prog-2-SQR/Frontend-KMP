package ar.edu.um.prog2.quiroga.turnosapp.data.remote.dto

import kotlinx.serialization.Serializable

/** Un profesional en GET /api/professionals/search del catálogo. */
@Serializable
data class ProfessionalDto(
    val id: Long,
    val categoryId: Long,
    val firstName: String,
    val lastName: String,
    val enabled: Boolean = true,
)

/** Página de la búsqueda: {content, page, size, totalElements, totalPages}. */
@Serializable
data class ProfessionalPageDto(
    val content: List<ProfessionalDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

/** Una categoría en GET /api/categories del catálogo. */
@Serializable
data class CategoryDto(
    val id: Long,
    val name: String,
    val enabled: Boolean = true,
)
