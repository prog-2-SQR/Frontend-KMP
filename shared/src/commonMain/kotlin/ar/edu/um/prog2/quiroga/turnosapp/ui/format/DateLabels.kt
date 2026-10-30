package ar.edu.um.prog2.quiroga.turnosapp.ui.format

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.Weekday
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

// Textos de fechas y horas en castellano para la pantalla. El nombre del día sale de Weekday (mismo orden y nombres).

private fun LocalDate.weekday(): Weekday = Weekday.valueOf(dayOfWeek.name)

/** "lunes 2/11": para las frases, como "Turnos del lunes 2/11". */
fun LocalDate.longLabel(): String = "${weekday().label.lowercase()} $day/${month.number}"

/** "09:00". */
fun LocalTime.label(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
