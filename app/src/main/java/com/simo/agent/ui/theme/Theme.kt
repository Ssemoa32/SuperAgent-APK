package com.simo.agent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SimoColorScheme = darkColorScheme(
    primary    = NeonCyan,
    secondary  = NeonPurple,
    background = DarkBackground,
    surface    = CardBackground,
    onPrimary  = Color.Black,
    onSecondary= Color.White,
    onBackground = TextPrimary,
    onSurface  = TextPrimary,
    error      = ErrorRed
)

@Composable
fun SimoAgentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SimoColorScheme,
        content = content
    )
}
