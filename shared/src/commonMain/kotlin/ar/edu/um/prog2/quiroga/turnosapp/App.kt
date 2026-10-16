package ar.edu.um.prog2.quiroga.turnosapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ar.edu.um.prog2.quiroga.turnosapp.ui.screens.HomeScreen
import ar.edu.um.prog2.quiroga.turnosapp.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    val homeScreen = remember { HomeScreen() }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            homeScreen.Content(modifier = Modifier.safeContentPadding())
        }
    }
}
