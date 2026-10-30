package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AvailableSlot
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FormField
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.calendarMillisToDate
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.label
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.longLabel
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.toCalendarMillis
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.AvailabilityUiState
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.AvailabilityViewModel
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.ConfirmationState

/**
 * Turnos libres de un profesional: calendario arriba, turnos del día elegido abajo (mañana y tarde) y un diálogo para
 * confirmar. Con la reserva creada muestra su estado. Solo dibuja el estado del ViewModel y le avisa lo que hace el usuario.
 */
class AvailabilityScreen {

    @Composable
    fun Content(viewModel: AvailabilityViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        // Toda la pantalla se desplaza: el calendario y los turnos de un día con muchos horarios entran igual.
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Header(state.professionalName, onBack)
            val reservation = state.reservation
            if (reservation == null) {
                Calendar(state, viewModel)
                Slots(state, viewModel)
            } else {
                ReservationResult(reservation, onBack)
            }
        }
        state.confirmation?.let { ConfirmationDialog(it, state, viewModel) }
    }

    @Composable
    private fun Header(professionalName: String, onBack: () -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(text = "Turnos de $professionalName", style = MaterialTheme.typography.headlineSmall)
        }
    }

    // El calendario de Material 3 es experimental (ExperimentalMaterial3Api). Solo deja tocar los días del rango del
    // ViewModel; cuando cambia el día elegido, se lo avisa al ViewModel, que carga los turnos.
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Calendar(state: AvailabilityUiState, viewModel: AvailabilityViewModel) {
        val selectableDates = remember(state.firstDate, state.lastDate) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = state.isSelectable(calendarMillisToDate(utcTimeMillis))

                override fun isSelectableYear(year: Int) = year in state.firstDate.year..state.lastDate.year
            }
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDate.toCalendarMillis(),
            selectableDates = selectableDates,
        )
        LaunchedEffect(datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis?.let { viewModel.onDateSelected(calendarMillisToDate(it)) }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            DatePicker(state = datePickerState, title = null, headline = null, showModeToggle = false)
        }
    }

    @Composable
    private fun Slots(state: AvailabilityUiState, viewModel: AvailabilityViewModel) {
        Text(text = "Turnos del ${state.selectedDate.longLabel()}", style = MaterialTheme.typography.titleMedium)
        when {
            state.isLoading -> Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.errorMessage != null -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::retry) { Text("Reintentar") }
            }
            state.showsNoSlots -> Text("No hay turnos libres ese día. Probá con otro.")
            else -> {
                SlotGroup("Mañana", state.morningSlots, viewModel)
                SlotGroup("Tarde", state.afternoonSlots, viewModel)
            }
        }
    }

    // Filas de SLOT_COLUMNS botones del mismo ancho. No es una grilla "lazy" porque está dentro de una columna que
    // ya se desplaza, y un día tiene pocos turnos.
    @Composable
    private fun SlotGroup(title: String, slots: List<AvailableSlot>, viewModel: AvailabilityViewModel) {
        if (slots.isEmpty()) return
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        slots.chunked(SLOT_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { slot ->
                    FilledTonalButton(
                        onClick = { viewModel.onSlotSelected(slot) },
                        modifier = Modifier.weight(1f).height(52.dp),
                    ) { Text(text = slot.startTime.label(), style = MaterialTheme.typography.titleMedium) }
                }
                // La última fila puede quedar incompleta: se rellena para que los botones no se estiren.
                repeat(SLOT_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }

    @Composable
    private fun ConfirmationDialog(confirmation: ConfirmationState, state: AvailabilityUiState, viewModel: AvailabilityViewModel) {
        AlertDialog(
            // Mientras se reserva, el diálogo no se cierra tocando afuera: el resultado tiene que verse.
            onDismissRequest = { if (!confirmation.isReserving) viewModel.onConfirmationDismissed() },
            title = { Text("Confirmar turno") },
            text = { ConfirmationText(confirmation, state, viewModel) },
            confirmButton = {
                Button(onClick = viewModel::confirmReservation, enabled = confirmation.canConfirm) {
                    Text(if (confirmation.isReserving) "Reservando…" else "Reservar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onConfirmationDismissed, enabled = !confirmation.isReserving) { Text("Cancelar") }
            },
        )
    }

    @Composable
    private fun ConfirmationText(confirmation: ConfirmationState, state: AvailabilityUiState, viewModel: AvailabilityViewModel) {
        val formField = remember { FormField() }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "${state.professionalName}, ${state.selectedDate.longLabel()} de " +
                    "${confirmation.slot.startTime.label()} a ${confirmation.slot.endTime.label()}.",
            )
            if (confirmation.askPatientName) {
                formField.Content(label = "Nombre", value = confirmation.patientFirstName, onValueChange = viewModel::onPatientFirstNameChange)
                formField.Content(label = "Apellido", value = confirmation.patientLastName, onValueChange = viewModel::onPatientLastNameChange)
            }
            confirmation.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        }
    }

    @Composable
    private fun ReservationResult(reservation: Reservation, onBack: () -> Unit) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = reservation.status.label, style = MaterialTheme.typography.titleMedium)
                Text("Turno del ${reservation.date.longLabel()} a las ${reservation.startTime.label()}.")
                if (reservation.status == ReservationStatus.WAITING_FOR_PHONE) {
                    Text(
                        text = "El sistema central te va a pedir un teléfono para confirmar la reserva.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Button(onClick = onBack) { Text("Volver a la búsqueda") }
    }

    private companion object {
        const val SLOT_COLUMNS = 3
    }
}
