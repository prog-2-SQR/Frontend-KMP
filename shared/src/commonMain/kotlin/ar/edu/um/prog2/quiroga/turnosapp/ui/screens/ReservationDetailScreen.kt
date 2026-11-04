package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FormField
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.ReservationStatusText
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.label
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.longLabel
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.ReservationDetailUiState
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.ReservationDetailViewModel

/**
 * El estado de una reserva, que se actualiza solo mientras está en curso. Cuando la cátedra pide el teléfono, muestra
 * su mensaje y un campo para escribirlo; un turno confirmado se puede cancelar. Solo dibuja el estado del ViewModel y
 * le avisa lo que hace el usuario.
 */
class ReservationDetailScreen {

    @Composable
    fun Content(viewModel: ReservationDetailViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text(text = "Mi reserva", style = MaterialTheme.typography.headlineSmall)
            }
            val reservation = state.reservation
            when {
                reservation != null -> {
                    Summary(state.professionalName, reservation)
                    Progress(reservation)
                    if (state.asksForPhone) PhoneForm(state, viewModel)
                    if (reservation.isCancellable) CancelButton(state, viewModel)
                }
                state.isLoading -> CircularProgressIndicator()
                else -> Unit
            }
            state.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        }
        if (state.isConfirmingCancel) CancelDialog(state, viewModel)
    }

    @Composable
    private fun Summary(professionalName: String, reservation: Reservation) {
        val statusText = remember { ReservationStatusText() }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = professionalName, style = MaterialTheme.typography.titleMedium)
                Text("${reservation.date.longLabel()} a las ${reservation.startTime.label()}")
                statusText.Content(reservation.status, style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    // Qué está pasando y qué tiene que hacer el usuario, según el estado. Mientras sigue en curso, una barra indica
    // que la app sigue consultando.
    @Composable
    private fun Progress(reservation: Reservation) {
        Text(explanationOf(reservation))
        if (reservation.isPending && !reservation.phoneRequested) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    private fun explanationOf(reservation: Reservation): String = when (reservation.status) {
        ReservationStatus.WAITING_FOR_PHONE ->
            if (reservation.phoneRequested) "El sistema central necesita tu teléfono para confirmar el turno."
            else "Esperando que el sistema central pida tu teléfono…"
        ReservationStatus.PHONE_SUBMITTED -> "Recibimos tu teléfono. Esperando la confirmación del sistema central…"
        ReservationStatus.CONFIRMED -> "¡Listo! Tu turno está confirmado."
        ReservationStatus.EXPIRED -> "El turno venció antes de confirmarse. Podés pedir otro."
        ReservationStatus.CANCELLED -> "La reserva fue cancelada."
        ReservationStatus.REQUESTED, ReservationStatus.HELD -> "Estamos reservando el turno…"
        else -> "No se pudo completar la reserva. Podés pedir otro turno."
    }

    @Composable
    private fun PhoneForm(state: ReservationDetailUiState, viewModel: ReservationDetailViewModel) {
        val formField = remember { FormField() }
        val reservation = state.reservation ?: return
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reservation.phoneRequestMessage?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
            reservation.phoneRejectionMessage?.let { Text(text = "El teléfono anterior no fue aceptado: $it", color = MaterialTheme.colorScheme.error) }
            formField.Content(
                label = "Teléfono",
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                error = state.phoneError,
                keyboardType = KeyboardType.Phone,
            )
            Button(onClick = viewModel::submitPhone, enabled = state.canSubmitPhone) {
                Text(if (state.isSubmittingPhone) "Enviando…" else "Enviar teléfono")
            }
        }
    }

    @Composable
    private fun CancelButton(state: ReservationDetailUiState, viewModel: ReservationDetailViewModel) {
        OutlinedButton(
            onClick = viewModel::onCancelRequested,
            enabled = state.canCancel,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        ) { Text("Cancelar turno") }
    }

    @Composable
    private fun CancelDialog(state: ReservationDetailUiState, viewModel: ReservationDetailViewModel) {
        val reservation = state.reservation ?: return
        AlertDialog(
            // Mientras se cancela, el diálogo no se cierra tocando afuera: el resultado tiene que verse.
            onDismissRequest = viewModel::onCancelDismissed,
            title = { Text("¿Cancelar el turno?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "${state.professionalName}, ${reservation.date.longLabel()} a las ${reservation.startTime.label()}. " +
                            "El turno queda libre para otra persona.",
                    )
                    state.cancelError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmCancel,
                    enabled = state.canCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text(if (state.isCancelling) "Cancelando…" else "Cancelar turno") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onCancelDismissed, enabled = !state.isCancelling) { Text("Volver") }
            },
        )
    }
}
