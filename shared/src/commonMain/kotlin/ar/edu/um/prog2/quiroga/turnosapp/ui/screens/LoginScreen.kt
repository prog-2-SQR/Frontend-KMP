package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FormField
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.SubmitButton
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.LoginViewModel

/** Inicio de sesión. Solo dibuja el estado del ViewModel y le avisa lo que hace el usuario. */
class LoginScreen {

    @Composable
    fun Content(
        viewModel: LoginViewModel,
        onLoggedIn: (AuthSession) -> Unit,
        onGoToRegister: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val formField = remember { FormField() }
        val submitButton = remember { SubmitButton() }

        // Cuando aparece la sesión, se avisa hacia arriba una sola vez: App la guarda y muestra la app.
        LaunchedEffect(state.session) {
            state.session?.let(onLoggedIn)
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
            formField.Content(label = "Usuario", value = state.username, onValueChange = viewModel::onUsernameChange)
            formField.Content(
                label = "Contraseña",
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                isPassword = true,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.rememberMe, onCheckedChange = viewModel::onRememberMeChange)
                Text(text = "Recordarme")
            }
            state.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            submitButton.Content(
                text = "Ingresar",
                onClick = viewModel::login,
                enabled = state.canSubmit,
                isLoading = state.isLoading,
            )
            TextButton(onClick = onGoToRegister) { Text("¿No tenés cuenta? Registrate") }
        }
    }
}
