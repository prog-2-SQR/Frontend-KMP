package ar.edu.um.prog2.quiroga.turnosapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ar.edu.um.prog2.quiroga.turnosapp.ui.navigation.AppDestination

/**
 * Barra de navegación inferior: una pestaña por cada [AppDestination]. Una pestaña está marcada si su ruta está entre
 * las de la pantalla actual (la propia y la de los grafos que la contienen).
 */
class AppBottomBar {

    @Composable
    fun Content(
        destinations: List<AppDestination>,
        currentRoutes: Set<String>,
        onSelect: (AppDestination) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        NavigationBar(modifier = modifier) {
            destinations.forEach { destination ->
                NavigationBarItem(
                    selected = destination.route in currentRoutes,
                    onClick = { onSelect(destination) },
                    icon = { Icon(imageVector = destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                )
            }
        }
    }
}
