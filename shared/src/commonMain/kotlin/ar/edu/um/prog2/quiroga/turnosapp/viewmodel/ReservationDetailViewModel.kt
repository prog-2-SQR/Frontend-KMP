package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.validation.PhoneRules
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Todo lo que muestra el detalle de una reserva. La pantalla solo lo dibuja. */
data class ReservationDetailUiState(
    val professionalName: String,
    val reservation: Reservation? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isSubmittingPhone: Boolean = false,
) {
    /** La cátedra pidió el teléfono: la pantalla muestra el campo para escribirlo. */
    val asksForPhone: Boolean get() = reservation?.phoneRequested == true
    val canSubmitPhone: Boolean get() = asksForPhone && !isSubmittingPhone && phone.isNotBlank()
}

/**
 * El estado de una reserva. Mientras está en curso se vuelve a consultar cada pollInterval (Turnos todavía no tiene
 * SSE: recorte aceptado en el plan); cuando termina, deja de consultar. Cuando la cátedra pide el teléfono, lo valida
 * con la misma regla que Turnos y lo manda.
 */
class ReservationDetailViewModel(
    private val reservationId: String,
    professionalName: String,
    private val repository: AppointmentRepository,
    private val pollIntervalMillis: Long = POLL_INTERVAL_MILLIS,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailUiState(professionalName))
    val uiState: StateFlow<ReservationDetailUiState> = _uiState.asStateFlow()

    // Una sola consulta periódica a la vez: se corta mientras se manda el teléfono y se reinicia después.
    private var followJob: Job? = null

    init {
        follow()
    }

    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, phoneError = null) }

    fun submitPhone() {
        val state = _uiState.value
        if (!state.canSubmitPhone) return
        val error = PhoneRules.phoneError(state.phone)
        if (error != null) {
            _uiState.update { it.copy(phoneError = error) }
            return
        }
        followJob?.cancel()
        _uiState.update { it.copy(isSubmittingPhone = true, phoneError = null) }
        viewModelScope.launch {
            repository.submitPhone(reservationId, state.phone)
                .onSuccess { reservation -> _uiState.update { it.copy(isSubmittingPhone = false, reservation = reservation, phone = "") } }
                .onFailure { error -> _uiState.update { it.copy(isSubmittingPhone = false, phoneError = ErrorMessages.of(error)) } }
            // Después de mandarlo, se sigue consultando hasta el resultado.
            follow()
        }
    }

    // Una consulta y, mientras la reserva siga en curso (o no se pudo leer), otra cada pollInterval. Termina sola cuando
    // la reserva llega a un estado final, y se cancela cuando se cierra la pantalla (viewModelScope).
    private fun follow() {
        followJob?.cancel()
        followJob = viewModelScope.launch {
            while (true) {
                refresh()
                if (_uiState.value.reservation?.isPending == false) return@launch
                delay(pollIntervalMillis)
            }
        }
    }

    private suspend fun refresh() {
        repository.reservation(reservationId)
            .onSuccess { reservation -> _uiState.update { it.copy(isLoading = false, errorMessage = null, reservation = reservation) } }
            .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMessages.of(error)) } }
    }

    companion object {
        const val POLL_INTERVAL_MILLIS = 3_000L
    }
}
