package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/**
 * Filtros de la búsqueda (enunciado §4.1: categoría, nombre, habilitado y disponibilidad). Los vacíos no filtran.
 * onlyEnabled = false muestra también los deshabilitados.
 */
data class ProfessionalFilters(
    val name: String = "",
    val categoryId: Long? = null,
    val day: Weekday? = null,
    val onlyEnabled: Boolean = true,
)
