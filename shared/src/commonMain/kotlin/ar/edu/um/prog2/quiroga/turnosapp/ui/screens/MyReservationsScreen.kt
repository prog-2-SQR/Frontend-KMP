package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Reservation
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.ReservationStatusText
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.label
import ar.edu.um.prog2.quiroga.turnosapp.ui.format.longLabel
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.MyReservationsUiState
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.MyReservationsViewModel

/** Las reservas del usuario, de la más nueva a la más vieja. Tocando una se abre su detalle. */
class MyReservationsScreen {

    @Composable
    fun Content(
        viewModel: MyReservationsViewModel,
        onReservationSelected: (Reservation, String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        // Al volver a la pestaña (por ejemplo, después de reservar) se recarga: los estados pueden haber cambiado.
        LaunchedEffect(viewModel) { viewModel.refresh() }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Mis reservas", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = viewModel::refresh, enabled = !state.isLoading) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Actualizar")
                }
            }
            Reservations(state, viewModel, onReservationSelected)
            Pagination(state, viewModel)
        }
    }

    // ColumnScope: la lista ocupa el espacio que deja la paginación (weight).
    @Composable
    private fun ColumnScope.Reservations(
        state: MyReservationsUiState,
        viewModel: MyReservationsViewModel,
        onReservationSelected: (Reservation, String) -> Unit,
    ) {
        val area = Modifier.fillMaxWidth().weight(1f)
        val statusText = remember { ReservationStatusText() }
        when {
            state.isLoading && state.reservations.isEmpty() -> Box(area, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.errorMessage != null -> Column(area, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::refresh) { Text("Reintentar") }
            }
            state.showsNoReservations -> Text(text = "Todavía no tenés reservas. Buscá un profesional para pedir un turno.", modifier = area)
            else -> LazyColumn(modifier = area) {
                items(state.reservations, key = Reservation::id) { reservation ->
                    val professionalName = state.professionalName(reservation.professionalId)
                    ListItem(
                        headlineContent = { Text(professionalName) },
                        supportingContent = { Text("${reservation.date.longLabel()} a las ${reservation.startTime.label()}") },
                        trailingContent = { statusText.Content(reservation.status) },
                        modifier = Modifier.clickable { onReservationSelected(reservation, professionalName) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    @Composable
    private fun Pagination(state: MyReservationsUiState, viewModel: MyReservationsViewModel) {
        val result = state.result
        if (result.totalPages <= 1) return
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = viewModel::previousPage, enabled = result.hasPrevious && !state.isLoading) { Text("Anterior") }
            Text(text = "Página ${result.page + 1} de ${result.totalPages}")
            TextButton(onClick = viewModel::nextPage, enabled = result.hasNext && !state.isLoading) { Text("Siguiente") }
        }
    }
}
