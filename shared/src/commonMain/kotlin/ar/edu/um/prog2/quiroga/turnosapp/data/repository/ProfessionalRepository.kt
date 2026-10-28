package ar.edu.um.prog2.quiroga.turnosapp.data.repository

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Category
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalPage

/** Búsqueda de profesionales en el catálogo. El ViewModel depende de esta interfaz; los tests usan una falsa. */
interface ProfessionalRepository {

    suspend fun search(filters: ProfessionalFilters, page: Int): Result<ProfessionalPage>

    suspend fun categories(): Result<List<Category>>
}

/** Error de la búsqueda con un mensaje para mostrar al usuario. */
class CatalogException(message: String) : Exception(message)
