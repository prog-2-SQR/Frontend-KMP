package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.ProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Category
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Professional
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalPage
import kotlinx.coroutines.CompletableDeferred

/** Catálogo falso para los tests del ViewModel: devuelve lo configurado y anota cada búsqueda. */
class FakeProfessionalRepository : ProfessionalRepository {

    var searchResult: Result<ProfessionalPage> = Result.success(page(0, totalPages = 3))
    var categoriesResult: Result<List<Category>> = Result.success(listOf(Category(id = 101, name = "Cardiología")))

    // Si está, la búsqueda espera a que el test la complete (para ver el estado "cargando").
    var gate: CompletableDeferred<Unit>? = null

    val searches = mutableListOf<Pair<ProfessionalFilters, Int>>()

    override suspend fun search(filters: ProfessionalFilters, page: Int): Result<ProfessionalPage> {
        searches += filters to page
        gate?.await()
        return searchResult.map { it.copy(page = page) }
    }

    override suspend fun categories(): Result<List<Category>> = categoriesResult

    companion object {
        val ANA = Professional(id = 201, categoryId = 101, firstName = "Ana", lastName = "Torres", enabled = true)

        fun page(page: Int, totalPages: Int, professionals: List<Professional> = listOf(ANA)) =
            ProfessionalPage(professionals = professionals, page = page, totalPages = totalPages, totalElements = professionals.size.toLong())
    }
}
