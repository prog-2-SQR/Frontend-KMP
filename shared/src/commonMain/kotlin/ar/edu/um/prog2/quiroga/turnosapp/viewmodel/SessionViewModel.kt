package ar.edu.um.prog2.quiroga.turnosapp.viewmodel

import androidx.lifecycle.ViewModel
import ar.edu.um.prog2.quiroga.turnosapp.data.repository.AuthRepository
import ar.edu.um.prog2.quiroga.turnosapp.domain.model.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * La sesión de toda la app: sin sesión se ve el login; con sesión, las pestañas. Arranca con la que quedó guardada con
 * "Recordarme", si sigue vigente. Como ViewModel, sobrevive a la rotación de pantalla.
 */
class SessionViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _session = MutableStateFlow(authRepository.savedSession())
    val session: StateFlow<AuthSession?> = _session.asStateFlow()

    fun onLoggedIn(session: AuthSession) {
        _session.value = session
    }

    fun logout() {
        authRepository.logout()
        _session.value = null
    }
}
