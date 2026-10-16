package ar.edu.um.prog2.quiroga.turnosapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Paleta de la app: tonos turquesa y azul, asociados a servicios de salud. */
object AppColors {
    private val Teal40 = Color(0xFF006A6A)
    private val Teal80 = Color(0xFF4CDADA)
    private val Blue40 = Color(0xFF3E5F90)
    private val Blue80 = Color(0xFFA8C8FF)
    private val Coral40 = Color(0xFF9A4524)
    private val Coral80 = Color(0xFFFFB59A)

    val Light: ColorScheme = lightColorScheme(
        primary = Teal40,
        secondary = Blue40,
        tertiary = Coral40,
    )

    val Dark: ColorScheme = darkColorScheme(
        primary = Teal80,
        secondary = Blue80,
        tertiary = Coral80,
    )
}
