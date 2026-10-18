package ar.edu.um.prog2.quiroga.turnosapp.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class AppDestinationTest {

    @Test
    fun routesAreUnique() {
        val routes = AppDestination.entries.map { it.route }

        assertEquals(routes.size, routes.toSet().size, "Hay rutas repetidas: $routes")
    }

    @Test
    fun startDestinationIsHome() {
        assertEquals(AppDestination.Home, AppDestination.start)
    }
}
