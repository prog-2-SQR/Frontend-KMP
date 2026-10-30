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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Category
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Professional
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Weekday
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FilterDropdown
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.FormField
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.SearchUiState
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.SearchViewModel

/** Búsqueda de profesionales con filtros y paginación. Solo dibuja el estado del ViewModel y le avisa lo que hace el usuario. */
class SearchScreen {

    @Composable
    fun Content(viewModel: SearchViewModel, onProfessionalSelected: (Professional) -> Unit, modifier: Modifier = Modifier) {
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Buscar profesionales", style = MaterialTheme.typography.headlineSmall)
            Filters(state, viewModel)
            Results(state, onProfessionalSelected)
            Pagination(state, viewModel)
        }
    }

    @Composable
    private fun Filters(state: SearchUiState, viewModel: SearchViewModel) {
        val formField = remember { FormField() }
        val filterDropdown = remember { FilterDropdown() }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            formField.Content(
                label = "Nombre",
                value = state.filters.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.weight(1f),
            )
            Button(onClick = viewModel::search, enabled = !state.isLoading) { Text("Buscar") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filterDropdown.Content(
                label = "Categoría",
                options = state.categories,
                selected = state.categories.firstOrNull { it.id == state.filters.categoryId },
                optionLabel = Category::name,
                onSelect = { category -> viewModel.onCategoryChange(category?.id) },
                allLabel = "Todas",
            )
            filterDropdown.Content(
                label = "Día",
                options = Weekday.entries,
                selected = state.filters.day,
                optionLabel = Weekday::label,
                onSelect = viewModel::onDayChange,
                allLabel = "Todos",
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.filters.onlyEnabled, onCheckedChange = viewModel::onOnlyEnabledChange)
            Text(text = "Solo habilitados")
        }
    }

    // ColumnScope: la lista ocupa el espacio que dejan los filtros y la paginación (weight).
    @Composable
    private fun ColumnScope.Results(state: SearchUiState, onProfessionalSelected: (Professional) -> Unit) {
        val area = Modifier.fillMaxWidth().weight(1f)
        when {
            state.isLoading -> Box(area, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.errorMessage != null -> Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error, modifier = area)
            state.showsNoResults -> Text(text = "No hay profesionales con esos filtros.", modifier = area)
            else -> LazyColumn(modifier = area) {
                items(state.professionals, key = Professional::id) { professional ->
                    ProfessionalItem(professional, state.categoryName(professional.categoryId), onProfessionalSelected)
                    HorizontalDivider()
                }
            }
        }
    }

    // Solo un profesional habilitado lleva a sus turnos: uno deshabilitado se ve, pero no se puede reservar.
    @Composable
    private fun ProfessionalItem(professional: Professional, categoryName: String?, onSelected: (Professional) -> Unit) {
        ListItem(
            headlineContent = { Text(professional.fullName) },
            supportingContent = categoryName?.let { { Text(it) } },
            trailingContent = {
                if (professional.enabled) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Ver turnos")
                } else {
                    Text(text = "Deshabilitado", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = if (professional.enabled) Modifier.clickable { onSelected(professional) } else Modifier,
        )
    }

    @Composable
    private fun Pagination(state: SearchUiState, viewModel: SearchViewModel) {
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
