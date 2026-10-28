package ar.edu.um.prog2.quiroga.turnosapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Filtro de una opción: un botón con la opción elegida que abre la lista. La primera entrada (allLabel) quita
 * el filtro. Sirve para cualquier tipo de opción: optionLabel dice cómo mostrar cada una.
 */
class FilterDropdown {

    @Composable
    fun <T> Content(
        label: String,
        options: List<T>,
        selected: T?,
        optionLabel: (T) -> String,
        onSelect: (T?) -> Unit,
        allLabel: String,
        modifier: Modifier = Modifier,
    ) {
        // Si el menú está abierto es estado de la vista, no del ViewModel: se recuerda entre recomposiciones.
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            OutlinedButton(onClick = { expanded = true }) {
                Text("$label: ${selected?.let(optionLabel) ?: allLabel}")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text(allLabel) }, onClick = { expanded = false; onSelect(null) })
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(optionLabel(option)) }, onClick = { expanded = false; onSelect(option) })
                }
            }
        }
    }
}
