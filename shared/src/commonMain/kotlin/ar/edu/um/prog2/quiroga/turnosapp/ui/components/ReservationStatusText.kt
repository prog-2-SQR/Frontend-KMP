package ar.edu.um.prog2.quiroga.turnosapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus

/** El estado de una reserva con un color que se entiende de un vistazo: confirmada, en curso o terminada sin turno. */
class ReservationStatusText {

    @Composable
    fun Content(status: ReservationStatus, modifier: Modifier = Modifier, style: TextStyle = MaterialTheme.typography.labelLarge) {
        Text(text = status.label, color = colorOf(status), style = style, modifier = modifier)
    }

    @Composable
    private fun colorOf(status: ReservationStatus): Color = when {
        status == ReservationStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        status.isPending -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
}
