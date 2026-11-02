package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.ProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Todo lo que muestra la pestaña Mis reservas. La pantalla solo lo dibuja. */
data class MyReservationsUiState(
    val result: ReservationPage = ReservationPage.EMPTY,
    /** Nombre de cada profesional por id: Turnos solo guarda el id, el nombre se pide al catálogo. */
    val professionalNames: Map<Long, String> = emptyMap(),
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val reservations: List<Reservation> get() = result.reservations
    val showsNoReservations: Boolean get() = hasLoaded && !isLoading && errorMessage == null && reservations.isEmpty()

    fun professionalName(id: Long): String = professionalNames[id] ?: "Profesional $id"
}

/** Las reservas del usuario, de la más nueva a la más vieja, con paginación. */
class MyReservationsViewModel(
    private val appointmentRepository: AppointmentRepository,
    private val professionalRepository: ProfessionalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReservationsUiState())
    val uiState: StateFlow<MyReservationsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    /** Vuelve a la primera página: así se ve una reserva recién hecha o un estado que cambió. */
    fun refresh() = load(page = 0)

    fun nextPage() {
        val result = _uiState.value.result
        if (result.hasNext) load(result.page + 1)
    }

    fun previousPage() {
        val result = _uiState.value.result
        if (result.hasPrevious) load(result.page - 1)
    }

    private fun load(page: Int) {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob = viewModelScope.launch {
            appointmentRepository.myReservations(page)
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, hasLoaded = true, result = result) }
                    loadMissingNames(result)
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMessages.of(error)) } }
        }
    }

    // Un pedido por profesional que todavía no se conoce. Si el catálogo no responde, se muestra el número.
    private suspend fun loadMissingNames(result: ReservationPage) {
        val missing = result.reservations.map(Reservation::professionalId).distinct() - _uiState.value.professionalNames.keys
        missing.forEach { id ->
            professionalRepository.professional(id).onSuccess { professional ->
                _uiState.update { it.copy(professionalNames = it.professionalNames + (id to professional.fullName)) }
            }
        }
    }
}
