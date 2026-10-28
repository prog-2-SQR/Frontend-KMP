package ar.edu.um.prog2.quiroga.turnosapp.domain.model

/** Una página de resultados. page empieza en 0, como en el backend. */
data class ProfessionalPage(
    val professionals: List<Professional>,
    val page: Int,
    val totalPages: Int,
    val totalElements: Long,
) {
    val hasPrevious: Boolean get() = page > 0
    val hasNext: Boolean get() = page + 1 < totalPages

    companion object {
        val EMPTY = ProfessionalPage(professionals = emptyList(), page = 0, totalPages = 0, totalElements = 0)
    }
}
