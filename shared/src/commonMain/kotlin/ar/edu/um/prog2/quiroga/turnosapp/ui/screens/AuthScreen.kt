package ar.edu.um.prog2.quiroga.turnosapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.ui.navigation.AuthDestination
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.LoginViewModel
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.RegisterViewModel

/** Lo que se ve sin sesión: login y registro. Cada pantalla tiene su ViewModel, que sobrevive a la rotación. */
class AuthScreen {

    @Composable
    fun Content(
        authRepository: AuthRepository,
        onLoggedIn: (AuthSession) -> Unit,
    ) {
        val navController = rememberNavController()
        val loginScreen = remember { LoginScreen() }
        val registerScreen = remember { RegisterScreen() }

        NavHost(navController = navController, startDestination = AuthDestination.start.route) {
            composable(AuthDestination.Login.route) {
                val loginViewModel = viewModel { LoginViewModel(authRepository) }
                loginScreen.Content(
                    viewModel = loginViewModel,
                    onLoggedIn = onLoggedIn,
                    onGoToRegister = { navController.navigate(AuthDestination.Register.route) },
                )
            }
            composable(AuthDestination.Register.route) {
                val registerViewModel = viewModel { RegisterViewModel(authRepository) }
                registerScreen.Content(
                    viewModel = registerViewModel,
                    onRegistered = { navController.popBackStack() },
                    onGoToLogin = { navController.popBackStack() },
                )
            }
        }
    }
}
