package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.ProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.ui.components.AppBottomBar
import ar.edu.um.prog2.quiroga.turnosapp.ui.navigation.AppDestination
import ar.edu.um.prog2.quiroga.turnosapp.ui.navigation.AvailabilityRoute
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.AvailabilityViewModel
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.SearchViewModel

/** Contenedor principal: barra inferior y el área donde se muestra la pestaña elegida. */
class MainScreen {

    @Composable
    fun Content(
        darkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        professionalRepository: ProfessionalRepository,
        appointmentRepository: AppointmentRepository,
    ) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        // Las rutas de la pantalla actual y de los grafos que la contienen: en los turnos de un profesional, la
        // pestaña Buscar sigue marcada porque esa pantalla está dentro de su grafo.
        val currentRoutes = backStackEntry?.destination?.hierarchy?.mapNotNull { it.route }?.toSet().orEmpty()

        val bottomBar = remember { AppBottomBar() }
        val homeScreen = remember { HomeScreen() }
        val settingsScreen = remember { SettingsScreen() }

        Scaffold(
            bottomBar = {
                bottomBar.Content(
                    destinations = AppDestination.entries,
                    currentRoutes = currentRoutes,
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
                searchGraph(navController, professionalRepository, appointmentRepository)
                composable(AppDestination.Settings.route) {
                    settingsScreen.Content(darkTheme = darkTheme, onDarkThemeChange = onDarkThemeChange)
                }
            }
        }
    }

    /**
     * La pestaña Buscar es un grafo con dos pantallas: la lista y los turnos del profesional elegido. Al volver
     * de los turnos, la búsqueda sigue como estaba (su ViewModel vive mientras la lista esté en la pila).
     */
    private fun NavGraphBuilder.searchGraph(
        navController: NavHostController,
        professionalRepository: ProfessionalRepository,
        appointmentRepository: AppointmentRepository,
    ) {
        navigation(startDestination = SEARCH_LIST_ROUTE, route = AppDestination.Search.route) {
            composable(SEARCH_LIST_ROUTE) {
                val searchScreen = remember { SearchScreen() }
                val searchViewModel = viewModel { SearchViewModel(professionalRepository) }
                searchScreen.Content(
                    viewModel = searchViewModel,
                    onProfessionalSelected = { professional ->
                        navController.navigate(AvailabilityRoute(professional.id, professional.fullName))
                    },
                )
            }
            composable<AvailabilityRoute> { entry ->
                val route = entry.toRoute<AvailabilityRoute>()
                val availabilityScreen = remember { AvailabilityScreen() }
                val availabilityViewModel = viewModel {
                    AvailabilityViewModel(route.professionalId, route.professionalName, appointmentRepository)
                }
                availabilityScreen.Content(viewModel = availabilityViewModel, onBack = { navController.popBackStack() })
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

    private companion object {
        const val SEARCH_LIST_ROUTE = "search/list"
    }
}
