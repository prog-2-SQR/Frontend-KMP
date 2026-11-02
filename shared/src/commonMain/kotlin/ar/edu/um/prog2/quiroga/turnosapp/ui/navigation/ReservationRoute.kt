package ar.edu.um.prog2.quiroga.turnosapp.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Detalle de una reserva, dentro de la pestaña Mis reservas. Se llega desde la lista o desde la reserva recién creada.
 * Lleva el nombre del profesional porque Turnos solo guarda su id.
 */
@Serializable
data class ReservationRoute(
    val reservationId: String,
    val professionalName: String,
)
