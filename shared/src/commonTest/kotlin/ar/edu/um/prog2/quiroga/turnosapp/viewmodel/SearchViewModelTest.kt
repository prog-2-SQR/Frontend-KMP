package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import ar.edu.um.prog2.quiroga.turnosapp.data.repository.CatalogException
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Weekday
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

// viewModelScope usa Dispatchers.Main, que en los tests no existe: se reemplaza por uno de prueba.
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val repository = FakeProfessionalRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithTheCategoriesAndTheFirstPageOfEnabledProfessionals() {
        val viewModel = SearchViewModel(repository)

        val state = viewModel.uiState.value
        assertEquals(listOf(ProfessionalFilters() to 0), repository.searches)
        assertTrue(state.filters.onlyEnabled)
        assertEquals("Ana Torres", state.professionals.single().fullName)
        assertEquals("Cardiología", state.categoryName(101))
        assertFalse(state.isLoading)
    }

    @Test
    fun typingTheNameDoesNotSearchUntilAsked() {
        val viewModel = SearchViewModel(repository)

        viewModel.onNameChange("ana")
        assertEquals(1, repository.searches.size)

        viewModel.search()
        assertEquals(ProfessionalFilters(name = "ana") to 0, repository.searches.last())
    }

    @Test
    fun categoryDayAndEnabledSearchRightAwayFromTheFirstPage() {
        val viewModel = SearchViewModel(repository)
        viewModel.nextPage()

        viewModel.onCategoryChange(101)
        viewModel.onDayChange(Weekday.MONDAY)
        viewModel.onOnlyEnabledChange(false)

        assertEquals(
            ProfessionalFilters(categoryId = 101, day = Weekday.MONDAY, onlyEnabled = false) to 0,
            repository.searches.last(),
        )
        assertEquals(5, repository.searches.size)
    }

    @Test
    fun pagesForwardAndBackWithinTheLimits() {
        val viewModel = SearchViewModel(repository)

        viewModel.previousPage()
        assertEquals(0, viewModel.uiState.value.result.page)

        viewModel.nextPage()
        viewModel.nextPage()
        assertEquals(2, viewModel.uiState.value.result.page)
        viewModel.nextPage()
        assertEquals(2, viewModel.uiState.value.result.page)

        viewModel.previousPage()
        assertEquals(1, viewModel.uiState.value.result.page)
        assertEquals(listOf(0, 1, 2, 1), repository.searches.map { it.second })
    }

    @Test
    fun showsLoadingWhileTheCatalogAnswers() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        val viewModel = SearchViewModel(repository)

        assertTrue(viewModel.uiState.value.isLoading)
        gate.complete(Unit)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun errorIsShownAndClearedByTheNextSearch() {
        repository.searchResult = Result.failure(CatalogException("No se pudo conectar con el catálogo. Probá de nuevo."))
        val viewModel = SearchViewModel(repository)
        assertEquals("No se pudo conectar con el catálogo. Probá de nuevo.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.showsNoResults)

        repository.searchResult = Result.success(FakeProfessionalRepository.page(0, totalPages = 1))
        viewModel.search()
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun emptyResultIsTold() {
        repository.searchResult = Result.success(FakeProfessionalRepository.page(0, totalPages = 0, professionals = emptyList()))

        val viewModel = SearchViewModel(repository)

        assertTrue(viewModel.uiState.value.showsNoResults)
    }

    @Test
    fun withoutCategoriesTheSearchStillWorks() {
        repository.categoriesResult = Result.failure(CatalogException("sin conexión"))

        val viewModel = SearchViewModel(repository)

        assertTrue(viewModel.uiState.value.categories.isEmpty())
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.categoryName(101))
        assertEquals(1, viewModel.uiState.value.professionals.size)
    }
}
