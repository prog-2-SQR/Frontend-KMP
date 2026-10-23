package ar.edu.um.prog2.quiroga.turnosapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Botón principal de un formulario: mientras se espera la respuesta muestra un indicador de carga. */
class SubmitButton {

    @Composable
    fun Content(
        text: String,
        onClick: () -> Unit,
        enabled: Boolean,
        isLoading: Boolean,
        modifier: Modifier = Modifier,
    ) {
        Button(onClick = onClick, enabled = enabled && !isLoading, modifier = modifier.fillMaxWidth()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text)
            }
        }
    }
}
