package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.ProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.AppBottomBar
import ar.edu.um.prog2.quiroga.turnosapp.ui.navigation.AppDestination
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.SearchViewModel

/** Contenedor principal: barra inferior y el área donde se muestra la pestaña elegida. */
class MainScreen {

    @Composable
    fun Content(
        darkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        professionalRepository: ProfessionalRepository,
    ) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        val bottomBar = remember { AppBottomBar() }
        val homeScreen = remember { HomeScreen() }
        val searchScreen = remember { SearchScreen() }
        val settingsScreen = remember { SettingsScreen() }

        Scaffold(
            bottomBar = {
                bottomBar.Content(
                    destinations = AppDestination.entries,
                    currentRoute = currentRoute,
                    onSelect = { destination -> navigateToTab(navController, destination) },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.start.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(AppDestination.Home.route) { homeScreen.Content() }
                composable(AppDestination.Search.route) {
                    val searchViewModel = viewModel { SearchViewModel(professionalRepository) }
                    searchScreen.Content(viewModel = searchViewModel)
                }
                composable(AppDestination.Settings.route) {
                    settingsScreen.Content(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
                }
            }
        }
    }

    /** Cambia de pestaña sin apilar pantallas repetidas y conservando el estado de cada una. */
    private fun navigateToTab(navController: NavHostController, destination: AppDestination) {
        navController.navigate(destination.route) {
            popUpTo(AppDestination.start.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
