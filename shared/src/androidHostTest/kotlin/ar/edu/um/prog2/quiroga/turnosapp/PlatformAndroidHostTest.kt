package ar.edu.um.prog2.quiroga.turnosapp

import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformAndroidHostTest {

    @Test
    fun platformNameIdentifiesAndroid() {
        val platform = getPlatform()

        assertTrue(platform.name.startsWith("Android"), "Nombre inesperado: ${platform.name}")
    }
}
