package com.pouriaquant.goldarb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pouriaquant.goldarb.security.AppVisualStyle

private val GraphiteDark = darkColorScheme(
    primary = Color(0xFF62D6F4), onPrimary = Color(0xFF002B36),
    primaryContainer = Color(0xFF123E49), onPrimaryContainer = Color(0xFFC7F3FF),
    secondary = Color(0xFFB8C8D0), onSecondary = Color(0xFF223036),
    tertiary = Color(0xFF68DBAD), onTertiary = Color(0xFF003828),
    background = Color(0xFF0D1117), onBackground = Color(0xFFF0F3F6),
    surface = Color(0xFF151B23), onSurface = Color(0xFFF0F3F6),
    surfaceVariant = Color(0xFF202833), onSurfaceVariant = Color(0xFFC3CAD3),
    outline = Color(0xFF84909E), error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
)

private val GraphiteLight = lightColorScheme(
    primary = Color(0xFF00677B), onPrimary = Color.White,
    primaryContainer = Color(0xFFB3EBF9), onPrimaryContainer = Color(0xFF002E39),
    secondary = Color(0xFF4B626B), onSecondary = Color.White,
    tertiary = Color(0xFF176B51), onTertiary = Color.White,
    background = Color(0xFFF4F6F8), onBackground = Color(0xFF15191D),
    surface = Color.White, onSurface = Color(0xFF15191D),
    surfaceVariant = Color(0xFFE7EBEF), onSurfaceVariant = Color(0xFF47515B),
    outline = Color(0xFF6F7A85), error = Color(0xFFBA1A1A), onError = Color.White,
)

private val AuroraDark = darkColorScheme(
    primary = Color(0xFF80DEEA), onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF074D57), onPrimaryContainer = Color(0xFFB2EBF2),
    secondary = Color(0xFFC7B6FF), onSecondary = Color(0xFF30205E),
    tertiary = Color(0xFF79DCA7), onTertiary = Color(0xFF003921),
    background = Color(0xFF08131A), onBackground = Color(0xFFE7F3F7),
    surface = Color(0xFF10212A), onSurface = Color(0xFFE7F3F7),
    surfaceVariant = Color(0xFF19303A), onSurfaceVariant = Color(0xFFC2D5DC),
    outline = Color(0xFF82979F), error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
)

private val AuroraLight = lightColorScheme(
    primary = Color(0xFF006A73), onPrimary = Color.White,
    primaryContainer = Color(0xFF9CF0FA), onPrimaryContainer = Color(0xFF002023),
    secondary = Color(0xFF62558A), onSecondary = Color.White,
    tertiary = Color(0xFF006C49), onTertiary = Color.White,
    background = Color(0xFFF1F8FA), onBackground = Color(0xFF101D21),
    surface = Color(0xFFFAFEFF), onSurface = Color(0xFF101D21),
    surfaceVariant = Color(0xFFDDEEF1), onSurfaceVariant = Color(0xFF3F575D),
    outline = Color(0xFF687D83), error = Color(0xFFBA1A1A), onError = Color.White,
)

private val PaperDark = darkColorScheme(
    primary = Color(0xFFFFD17A), onPrimary = Color(0xFF432C00),
    primaryContainer = Color(0xFF5E4100), onPrimaryContainer = Color(0xFFFFDEA3),
    secondary = Color(0xFFD8C3A5), onSecondary = Color(0xFF3B2F20),
    tertiary = Color(0xFF8FD5B1), onTertiary = Color(0xFF003824),
    background = Color(0xFF171512), onBackground = Color(0xFFF1EDE5),
    surface = Color(0xFF211E19), onSurface = Color(0xFFF1EDE5),
    surfaceVariant = Color(0xFF2E2922), onSurfaceVariant = Color(0xFFD5CEC3),
    outline = Color(0xFF978F83), error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
)

private val PaperLight = lightColorScheme(
    primary = Color(0xFF735700), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF91), onPrimaryContainer = Color(0xFF241A00),
    secondary = Color(0xFF675D4B), onSecondary = Color.White,
    tertiary = Color(0xFF256B4C), onTertiary = Color.White,
    background = Color(0xFFF8F4EC), onBackground = Color(0xFF1D1B17),
    surface = Color(0xFFFFFCF5), onSurface = Color(0xFF1D1B17),
    surfaceVariant = Color(0xFFEDE7DC), onSurfaceVariant = Color(0xFF514B42),
    outline = Color(0xFF777066), error = Color(0xFFBA1A1A), onError = Color.White,
)

@Composable
fun RasadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    visualStyle: AppVisualStyle = AppVisualStyle.GRAPHITE,
    content: @Composable () -> Unit,
) {
    val colors = when (visualStyle) {
        AppVisualStyle.GRAPHITE -> if (darkTheme) GraphiteDark else GraphiteLight
        AppVisualStyle.AURORA -> if (darkTheme) AuroraDark else AuroraLight
        AppVisualStyle.PAPER -> if (darkTheme) PaperDark else PaperLight
    }
    MaterialTheme(colorScheme = colors, typography = GoldArbTypography, content = content)
}
