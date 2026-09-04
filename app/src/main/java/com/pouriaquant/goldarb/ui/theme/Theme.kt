package com.pouriaquant.goldarb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pouriaquant.goldarb.security.AppVisualStyle

private val EmeraldDarkColors = darkColorScheme(
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

private val EmeraldLightColors = lightColorScheme(
    primary = Color(0xFF0D5C46),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEDE5),
    onPrimaryContainer = Color(0xFF07392C),
    secondary = Color(0xFF8A682B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF2E4C5),
    onSecondaryContainer = Color(0xFF4B3511),
    background = Color(0xFFF6F3EB),
    onBackground = Color(0xFF17201C),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF17201C),
    surfaceVariant = Color(0xFFECEFE9),
    onSurfaceVariant = Color(0xFF606A64),
    outline = Color(0xFFD8DED9),
)

private val NavyDarkColors = darkColorScheme(
    primary = Color(0xFFA9C9EA),
    onPrimary = Color(0xFF0B2D4B),
    primaryContainer = Color(0xFF17324F),
    onPrimaryContainer = Color(0xFFDCEBFA),
    secondary = Color(0xFFE0BE7A),
    onSecondary = Color(0xFF3E2D0A),
    background = Color(0xFF11151C),
    onBackground = Color(0xFFF6F1E5),
    surface = Color(0xFF1A202A),
    onSurface = Color(0xFFF6F1E5),
    surfaceVariant = Color(0xFF252D39),
    onSurfaceVariant = Color(0xFFB6BECA),
    outline = Color(0xFF87919F),
    error = Coral400,
)

private val NavyLightColors = lightColorScheme(
    primary = Color(0xFF17324F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE8F4),
    onPrimaryContainer = Color(0xFF0D2842),
    secondary = Color(0xFF8A682B),
    onSecondary = Color.White,
    background = Color(0xFFF4F1E8),
    onBackground = Color(0xFF19202A),
    surface = Color(0xFFFFFDF7),
    onSurface = Color(0xFF19202A),
    surfaceVariant = Color(0xFFE8E5DD),
    onSurfaceVariant = Color(0xFF636B77),
    outline = Color(0xFFD4D8DE),
)

@Composable
fun GoldArbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    visualStyle: AppVisualStyle = AppVisualStyle.EMERALD_LUXURY,
    content: @Composable () -> Unit,
) {
    val colors = when (visualStyle) {
        AppVisualStyle.EMERALD_LUXURY -> if (darkTheme) EmeraldDarkColors else EmeraldLightColors
        AppVisualStyle.NAVY_BANKING -> if (darkTheme) NavyDarkColors else NavyLightColors
    }
    MaterialTheme(
        colorScheme = colors,
        typography = GoldArbTypography,
        content = content,
    )
}
