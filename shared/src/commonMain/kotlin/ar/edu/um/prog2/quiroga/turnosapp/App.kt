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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.edu.um.prog2.quiroga.turnosapp.data.local.SettingsSessionStore
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.ApiConfig
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AppointmentApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.AuthApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.CatalogApi
import ar.edu.um.prog2.quiroga.turnosapp.data.remote.HttpClientFactory
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.KtorAppointmentRepository
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.KtorAuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.KtorProfessionalRepository
import ar.edu.um.prog2.quiroga.turnosapp.ui.screens.AuthScreen
import ar.edu.um.prog2.quiroga.turnosapp.ui.screens.MainScreen
import ar.edu.um.prog2.quiroga.turnosapp.ui.theme.AppTheme
import ar.edu.um.prog2.quiroga.turnosapp.viewmodel.SessionViewModel

@Composable
@Preview
fun App() {
    val systemDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemDarkTheme) }
    // Sin sesión se ven login y registro; con sesión, la app. La sesión la maneja SessionViewModel, arriba de todas las
    // pantallas: arranca con la guardada por "Recordarme" y vuelve a null al cerrar sesión.
    val authRepository = remember { KtorAuthRepository(AuthApi(HttpClientFactory.create()), SettingsSessionStore()) }
    val sessionViewModel = viewModel { SessionViewModel(authRepository) }
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    val authScreen = remember { AuthScreen() }
    val mainScreen = remember { MainScreen() }

    AppTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val currentSession = session
            if (currentSession == null) {
                authScreen.Content(authRepository = authRepository, onLoggedIn = sessionViewModel::onLoggedIn)
            } else {
                // El catálogo y Turnos reciben el token de esta sesión; si la sesión cambia, se arman otros repositorios.
                val professionalRepository = remember(currentSession) {
                    KtorProfessionalRepository(CatalogApi(HttpClientFactory.create(ApiConfig.CATALOGO_BASE_URL)), currentSession.token)
                }
                val appointmentRepository = remember(currentSession) {
                    KtorAppointmentRepository(AppointmentApi(HttpClientFactory.create(ApiConfig.TURNOS_BASE_URL)), currentSession.token)
                }
                mainScreen.Content(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it },
                    login = currentSession.login,
                    onLogout = sessionViewModel::logout,
                    professionalRepository = professionalRepository,
                    appointmentRepository = appointmentRepository,
                )
            }
        }
    }
}
