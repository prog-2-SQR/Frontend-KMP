package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.ProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Category
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Professional
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalFilters
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ProfessionalPage
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Weekday
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Todo lo que muestra la pantalla de búsqueda. La pantalla solo lo dibuja. */
data class SearchUiState(
    val filters: ProfessionalFilters = ProfessionalFilters(),
    val categories: List<Category> = emptyList(),
    val result: ProfessionalPage = ProfessionalPage.EMPTY,
    val hasSearched: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val professionals: List<Professional> get() = result.professionals
    val showsNoResults: Boolean get() = hasSearched && !isLoading && errorMessage == null && professionals.isEmpty()

    fun categoryName(categoryId: Long): String? = categories.firstOrNull { it.id == categoryId }?.name
}

/**
 * Búsqueda de profesionales en el catálogo. Categoría, día y "solo habilitados" buscan apenas cambian;
 * el nombre, cuando el usuario toca Buscar. Toda búsqueda nueva vuelve a la primera página.
 */
class SearchViewModel(
    private val repository: ProfessionalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // La búsqueda en curso: si llega otra antes de que termine, se cancela para no mostrar resultados viejos.
    private var searchJob: Job? = null

    init {
        loadCategories()
        search()
    }

    fun onNameChange(value: String) = updateFilters { it.copy(name = value) }

    fun onCategoryChange(categoryId: Long?) = updateFiltersAndSearch { it.copy(categoryId = categoryId) }

    fun onDayChange(day: Weekday?) = updateFiltersAndSearch { it.copy(day = day) }

    fun onOnlyEnabledChange(value: Boolean) = updateFiltersAndSearch { it.copy(onlyEnabled = value) }

    fun search() = load(page = 0)

    fun nextPage() {
        val result = _uiState.value.result
        if (result.hasNext) load(result.page + 1)
    }

    fun previousPage() {
        val result = _uiState.value.result
        if (result.hasPrevious) load(result.page - 1)
    }

    private fun updateFilters(change: (ProfessionalFilters) -> ProfessionalFilters) =
        _uiState.update { it.copy(filters = change(it.filters)) }

    private fun updateFiltersAndSearch(change: (ProfessionalFilters) -> ProfessionalFilters) {
        updateFilters(change)
        search()
    }

    private fun load(page: Int) {
        val filters = _uiState.value.filters
        searchJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        searchJob = viewModelScope.launch {
            repository.search(filters, page)
                .onSuccess { result -> _uiState.update { it.copy(isLoading = false, hasSearched = true, result = result) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMessages.of(error)) } }
        }
    }

    // Si las categorías no cargan, la búsqueda funciona igual, sin ese filtro.
    private fun loadCategories() {
        viewModelScope.launch {
            repository.categories().onSuccess { categories -> _uiState.update { it.copy(categories = categories) } }
        }
    }
}
