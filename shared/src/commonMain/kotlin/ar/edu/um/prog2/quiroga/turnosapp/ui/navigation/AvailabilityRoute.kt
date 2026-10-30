package ar.edu.um.prog2.quiroga.turnosapp.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Pantalla de turnos de un profesional, dentro de la pestaña Buscar. Es una ruta con tipo: Navigation arma la URL
 * con los dos datos y los devuelve con toRoute(), sin escribir a mano "availability/{id}" ni codificar el nombre.
 */
@Serializable
data class AvailabilityRoute(
    val professionalId: Long,
    val professionalName: String,
)
