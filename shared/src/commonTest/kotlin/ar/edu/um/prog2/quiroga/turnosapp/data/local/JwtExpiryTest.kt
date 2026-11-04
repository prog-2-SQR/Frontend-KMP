package ar.edu.um.prog2.quiroga.turnosapp.data.local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class JwtExpiryTest {

    @Test
    fun readsTheExpirationOfTheToken() {
        assertEquals(Instant.fromEpochSeconds(1_800_000_000), JwtExpiry.of(tokenExpiringAt(1_800_000_000)))
    }

    @Test
    fun aTokenWithoutTheExpectedFormatHasNoExpiration() {
        assertNull(JwtExpiry.of("no-es-un-jwt"))
        assertNull(JwtExpiry.of("abc.@@@.firma"))
    }
}
