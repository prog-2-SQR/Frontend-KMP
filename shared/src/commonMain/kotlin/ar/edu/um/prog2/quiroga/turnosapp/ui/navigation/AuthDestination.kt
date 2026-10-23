package ar.edu.um.prog2.quiroga.turnosapp.ui.navigation

/** Pantallas que se ven antes de iniciar sesión. */
enum class AuthDestination(val route: String) {
    Login(route = "login"),
    Register(route = "register");

    companion object {
        val start: AuthDestination = Login
    }
}
