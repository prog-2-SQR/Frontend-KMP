package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentException
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.NewReservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/** Todo lo que muestra la pantalla de turnos de un profesional. La pantalla solo lo dibuja. */
data class AvailabilityUiState(
    val professionalName: String,
    val days: List<LocalDate>,
    val selectedDate: LocalDate,
    val slots: List<AvailableSlot> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val confirmation: ConfirmationState? = null,
    val reservation: Reservation? = null,
) {
    val showsNoSlots: Boolean get() = !isLoading && errorMessage == null && slots.isEmpty()
}

/** El diálogo para confirmar un turno. Si la cuenta no tiene nombre, también los pide (askPatientName). */
data class ConfirmationState(
    val slot: AvailableSlot,
    val askPatientName: Boolean = false,
    val patientFirstName: String = "",
    val patientLastName: String = "",
    val isReserving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canConfirm: Boolean
        get() = !isReserving && (!askPatientName || (isValidName(patientFirstName) && isValidName(patientLastName)))

    private fun isValidName(name: String) = name.trim().length in NewReservation.PATIENT_NAME_LENGTH
}

/**
 * Turnos libres de un profesional y reserva. Muestra los próximos días (empieza por hoy), carga los turnos del día
 * elegido y, al confirmar uno, pide la reserva a Turnos. Con la reserva creada, la pantalla muestra su estado.
 */
class AvailabilityViewModel(
    private val professionalId: Long,
    professionalName: String,
    private val repository: AppointmentRepository,
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
) : ViewModel() {

    private val days = List(DAYS_SHOWN) { today.plus(DatePeriod(days = it)) }

    private val _uiState = MutableStateFlow(AvailabilityUiState(professionalName, days, selectedDate = today))
    val uiState: StateFlow<AvailabilityUiState> = _uiState.asStateFlow()

    // La carga en curso: si el usuario cambia de día antes de que termine, se cancela para no mostrar turnos de otro día.
    private var loadJob: Job? = null

    init {
        loadSlots()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadSlots()
    }

    fun retry() = loadSlots()

    fun onSlotSelected(slot: AvailableSlot) = _uiState.update { it.copy(confirmation = ConfirmationState(slot)) }

    fun onPatientFirstNameChange(value: String) = updateConfirmation { it.copy(patientFirstName = value) }

    fun onPatientLastNameChange(value: String) = updateConfirmation { it.copy(patientLastName = value) }

    // Si la reserva falló (por ejemplo, el turno se ocupó mientras tanto), al cerrar se vuelven a cargar los turnos.
    fun onConfirmationDismissed() {
        val failed = _uiState.value.confirmation?.errorMessage != null
        _uiState.update { it.copy(confirmation = null) }
        if (failed) loadSlots()
    }

    fun confirmReservation() {
        val state = _uiState.value
        val confirmation = state.confirmation ?: return
        if (!confirmation.canConfirm) return
        updateConfirmation { it.copy(isReserving = true, errorMessage = null) }
        viewModelScope.launch {
            repository.reserve(newReservation(state.selectedDate, confirmation))
                .onSuccess { reservation -> _uiState.update { it.copy(confirmation = null, reservation = reservation) } }
                .onFailure(::showReservationError)
        }
    }

    private fun newReservation(date: LocalDate, confirmation: ConfirmationState) = NewReservation(
        professionalId = professionalId,
        date = date,
        startTime = confirmation.slot.startTime,
        patientFirstName = confirmation.patientFirstName.trim().ifEmpty { null },
        patientLastName = confirmation.patientLastName.trim().ifEmpty { null },
    )

    private fun showReservationError(error: Throwable) {
        val needsPatientName = (error as? AppointmentException)?.needsPatientName == true
        updateConfirmation {
            it.copy(
                isReserving = false,
                errorMessage = ErrorMessages.of(error),
                askPatientName = it.askPatientName || needsPatientName,
            )
        }
    }

    private fun updateConfirmation(change: (ConfirmationState) -> ConfirmationState) =
        _uiState.update { state -> state.copy(confirmation = state.confirmation?.let(change)) }

    private fun loadSlots() {
        val date = _uiState.value.selectedDate
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, slots = emptyList()) }
        loadJob = viewModelScope.launch {
            repository.availability(professionalId, date)
                .onSuccess { slots -> _uiState.update { it.copy(isLoading = false, slots = slots) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMessages.of(error)) } }
        }
    }

    companion object {
        /** Cuántos días se ofrecen para elegir, empezando por hoy. */
        const val DAYS_SHOWN = 14
    }
}
