package ar.edu.um.prog2.quiroga.turnosapp.domain.validation

import ar.edu.um.prog2.quiroga.turnosapp.domain.model.ReservationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhoneRulesTest {

    @Test
    fun validPhonesWithSeparatorsAreAccepted() {
        listOf("+5492615551234", "+54 9 261 555-1234", "(261) 555.1234", "4251234").forEach {
            assertNull(PhoneRules.phoneError(it), it)
        }
    }

    @Test
    fun invalidPhonesHaveAMessage() {
        listOf("", "123456", "1234567890123456", "+54 9 261 abc", "++5492615551234").forEach {
            assertEquals(PhoneRules.INVALID_PHONE, PhoneRules.phoneError(it), it)
        }
    }

    @Test
    fun onlyTheStatesInProgressArePending() {
        assertTrue(ReservationStatus.WAITING_FOR_PHONE.isPending)
        assertTrue(ReservationStatus.PHONE_SUBMITTED.isPending)
        assertFalse(ReservationStatus.CONFIRMED.isPending)
        assertFalse(ReservationStatus.EXPIRED.isPending)
        assertFalse(ReservationStatus.UNKNOWN.isPending)
    }
}
