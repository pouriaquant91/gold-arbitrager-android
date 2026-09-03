package com.pouriaquant.goldarb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GoldArbColors = darkColorScheme(
    primary = Gold400,
    onPrimary = Pine950,
    primaryContainer = Pine800,
    onPrimaryContainer = Gold300,
    secondary = Mint400,
    onSecondary = Pine950,
    tertiary = Sky400,
    background = Pine950,
    onBackground = Ink50,
    surface = Pine900,
    onSurface = Ink50,
    surfaceVariant = Pine850,
    onSurfaceVariant = Ink200,
    outline = Outline,
    error = Coral400,
    onError = Pine950,
    scrim = Color.Black,
)

@Composable
fun GoldArbTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GoldArbColors,
        typography = GoldArbTypography,
        content = content,
    )
}

