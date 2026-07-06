package com.fred.termuxpanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Graphite0 = Color(0xFF0B0D10)
val Graphite1 = Color(0xFF15181D)
val Graphite2 = Color(0xFF1B1F25)
val Graphite3 = Color(0xFF242A32)
val Graphite4 = Color(0xFF2E353F)
val Amber = Color(0xFFF0A94E)
val Teal = Color(0xFF5FD0C3)
val Red = Color(0xFFF0785A)
val TextHi = Color(0xFFEEF1F4)
val TextLo = Color(0xFF8B96A3)
val LineColor = Color(0xFF333B45)

private val PanelColorScheme = darkColorScheme(
    background = Graphite1,
    surface = Graphite2,
    primary = Amber,
    secondary = Teal,
    error = Red,
    onBackground = TextHi,
    onSurface = TextHi,
    onPrimary = Color(0xFF1A1108),
)

@Composable
fun TermuxPanelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PanelColorScheme,
        content = content
    )
}
