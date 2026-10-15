package ar.edu.um.prog2.quiroga.turnosapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Aplica el tema de la app a todo lo que se dibuje dentro de [content]. */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AppColors.Dark else AppColors.Light,
        content = content,
    )
}
