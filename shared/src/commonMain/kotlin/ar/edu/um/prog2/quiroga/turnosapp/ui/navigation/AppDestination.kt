package ar.edu.um.prog2.quiroga.turnosapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Pestañas de la barra inferior. Cada entrada es un único objeto (singleton). */
enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home(route = "home", label = "Inicio", icon = Icons.Filled.Home),
    Search(route = "search", label = "Buscar", icon = Icons.Filled.Search),
    Settings(route = "settings", label = "Ajustes", icon = Icons.Filled.Settings);

    companion object {
        val start: AppDestination = Home
    }
}
