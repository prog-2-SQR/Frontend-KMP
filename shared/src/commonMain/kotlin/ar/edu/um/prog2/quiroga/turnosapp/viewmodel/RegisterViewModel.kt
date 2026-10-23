package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Registration
import ar.edu.um.prog2.quiroga.turnosapp.domain.validation.AccountRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Errores por campo del formulario de registro: null = el campo está bien. */
data class RegisterFieldErrors(
    val login: String? = null,
    val password: String? = null,
    val confirmation: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
) {
    val hasErrors: Boolean get() = listOfNotNull(login, password, confirmation, email, firstName, lastName).isNotEmpty()
}

data class RegisterUiState(
    val login: String = "",
    val password: String = "",
    val confirmation: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val fieldErrors: RegisterFieldErrors = RegisterFieldErrors(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registered: Boolean = false,
)

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // Al escribir en un campo se borra su error: el usuario ve que lo está corrigiendo.
    fun onLoginChange(value: String) =
        _uiState.update { it.copy(login = value, fieldErrors = it.fieldErrors.copy(login = null), errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, fieldErrors = it.fieldErrors.copy(password = null), errorMessage = null) }

    fun onConfirmationChange(value: String) =
        _uiState.update { it.copy(confirmation = value, fieldErrors = it.fieldErrors.copy(confirmation = null), errorMessage = null) }

    fun onFirstNameChange(value: String) =
        _uiState.update { it.copy(firstName = value, fieldErrors = it.fieldErrors.copy(firstName = null), errorMessage = null) }

    fun onLastNameChange(value: String) =
        _uiState.update { it.copy(lastName = value, fieldErrors = it.fieldErrors.copy(lastName = null), errorMessage = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, fieldErrors = it.fieldErrors.copy(email = null), errorMessage = null) }

    fun register() {
        val state = _uiState.value
        if (state.isLoading) return
        val errors = validate(state)
        if (errors.hasErrors) {
            _uiState.update { it.copy(fieldErrors = errors) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.register(toRegistration(state))
                .onSuccess { _uiState.update { it.copy(isLoading = false, registered = true) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMessages.of(error)) } }
        }
    }

    private fun validate(state: RegisterUiState) = RegisterFieldErrors(
        login = AccountRules.loginError(state.login.trim()),
        password = AccountRules.passwordError(state.password),
        confirmation = AccountRules.confirmationError(state.password, state.confirmation),
        email = AccountRules.emailError(state.email.trim()),
        firstName = AccountRules.nameError(state.firstName.trim()),
        lastName = AccountRules.nameError(state.lastName.trim()),
    )

    // Los campos opcionales vacíos no se mandan (null), como espera el backend.
    private fun toRegistration(state: RegisterUiState) = Registration(
        login = state.login.trim(),
        password = state.password,
        firstName = state.firstName.trim().ifEmpty { null },
        lastName = state.lastName.trim().ifEmpty { null },
        email = state.email.trim().ifEmpty { null },
    )
}
