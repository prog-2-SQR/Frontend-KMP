package ar.edu.um.prog2.quiroga.turnosapp.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// El calendario de Material 3 trabaja con milisegundos de la medianoche en UTC; la app, con LocalDate.
// Se convierte siempre en UTC para que un día no se corra al anterior por la zona horaria del teléfono.

fun LocalDate.toCalendarMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun calendarMillisToDate(millis: Long): LocalDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
