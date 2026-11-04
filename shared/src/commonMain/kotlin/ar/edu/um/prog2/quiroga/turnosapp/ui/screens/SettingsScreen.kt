package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Ajustes de la app: modo oscuro y cerrar sesión. El modo oscuro y la sesión viven en App: esta pantalla solo los
 * muestra y avisa lo que hace el usuario.
 */
class SettingsScreen {

    @Composable
    fun Content(
        darkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        login: String,
        onLogout: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Ajustes", style = MaterialTheme.typography.headlineMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Modo oscuro", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
            }
            HorizontalDivider()
            Text(text = "Sesión iniciada como $login", style = MaterialTheme.typography.bodyLarge)
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
        }
    }
}
