package ar.edu.um.prog2.quiroga.turnosapp.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class DateLabelsTest {

    @Test
    fun longLabelHasTheDayInSpanishAndLowercase() {
        assertEquals("viernes 30/10", LocalDate(2026, 10, 30).longLabel())
        assertEquals("miércoles 4/11", LocalDate(2026, 11, 4).longLabel())
        assertEquals("sábado 31/10", LocalDate(2026, 10, 31).longLabel())
    }

    @Test
    fun calendarDatesGoAndComeBackAsTheSameDay() {
        val date = LocalDate(2026, 11, 3)

        assertEquals(1_793_664_000_000L, date.toCalendarMillis())
        assertEquals(date, calendarMillisToDate(date.toCalendarMillis()))
    }

    @Test
    fun timesAlwaysHaveTwoDigits() {
        assertEquals("09:00", LocalTime(9, 0).label())
        assertEquals("14:30", LocalTime(14, 30, 15).label())
    }
}
