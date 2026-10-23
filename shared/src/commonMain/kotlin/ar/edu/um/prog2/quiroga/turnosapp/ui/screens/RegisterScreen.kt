package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FormField
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.SubmitButton
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.RegisterViewModel

/** Registro de un usuario nuevo. Las validaciones y el envío los hace el ViewModel. */
class RegisterScreen {

    @Composable
    fun Content(
        viewModel: RegisterViewModel,
        onRegistered: () -> Unit,
        onGoToLogin: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val errors = state.fieldErrors
        val formField = remember { FormField() }
        val submitButton = remember { SubmitButton() }

        LaunchedEffect(state.registered) {
            if (state.registered) onRegistered()
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)
            formField.Content("Usuario", state.login, viewModel::onLoginChange, error = errors.login)
            formField.Content("Contraseña", state.password, viewModel::onPasswordChange, error = errors.password, isPassword = true)
            formField.Content(
                "Repetir contraseña", state.confirmation, viewModel::onConfirmationChange,
                error = errors.confirmation, isPassword = true,
            )
            formField.Content("Nombre", state.firstName, viewModel::onFirstNameChange, error = errors.firstName)
            formField.Content("Apellido", state.lastName, viewModel::onLastNameChange, error = errors.lastName)
            formField.Content(
                "Email (opcional)", state.email, viewModel::onEmailChange,
                error = errors.email, keyboardType = KeyboardType.Email,
            )
            state.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            submitButton.Content(text = "Crear cuenta", onClick = viewModel::register, enabled = true, isLoading = state.isLoading)
            TextButton(onClick = onGoToLogin) { Text("Ya tengo cuenta") }
        }
    }
}
