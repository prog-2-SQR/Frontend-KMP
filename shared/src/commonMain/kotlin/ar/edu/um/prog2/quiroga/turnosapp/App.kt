package ar.edu.um.prog2.quiroga.turnosapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AuthApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.HttpClientFactory
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.KtorAuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import ar.edu.um.prog2.quiroga.turnosapp.ui.screens.AuthScreen
import ar.edu.um.prog2.quiroga.turnosapp.ui.screens.MainScreen
import ar.edu.um.prog2.quiroga.turnosapp.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }
    // Sin sesión se ven login y registro; con sesión, la app. La sesión vive acá, arriba de todas las pantallas.
    var session by remember { mutableStateOf<AuthSession?>(null) }
    val authRepository = remember { KtorAuthRepository(AuthApi(HttpClientFactory.create())) }
    val authScreen = remember { AuthScreen() }
    val mainScreen = remember { MainScreen() }

    AppTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (session == null) {
                authScreen.Content(authRepository = authRepository, onLoggedIn = { session = it })
            } else {
                mainScreen.Content(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it },
                )
            }
        }
    }
}
