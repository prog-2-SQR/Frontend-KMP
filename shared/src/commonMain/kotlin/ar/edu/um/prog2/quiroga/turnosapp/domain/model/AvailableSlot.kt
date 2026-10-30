package ar.edu.um.prog2.quiroga.turnosapp.domain.model

import kotlinx.datetime.LocalTime

/** Un turno libre de un profesional en un día: el horario semanal menos las ocupaciones de la cátedra. */
data class AvailableSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
)
