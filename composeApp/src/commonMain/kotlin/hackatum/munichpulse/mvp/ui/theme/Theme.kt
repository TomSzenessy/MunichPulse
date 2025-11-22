package hackatum.munichpulse.mvp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import hackatum.munichpulse.mvp.data.repository.SettingsRepository
import hackatum.munichpulse.mvp.ui.ProvideAppStrings

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = TextSecondary,
    secondary = TextSecondary,
    onSecondary = DarkBackground,
)

@Composable
fun UrbanPulseTheme(
    useDarkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    ProvideAppStrings {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color(0xFFF0F2F5), // Slightly off-white for better contrast
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE1E3E6),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF44474E),
    secondary = androidx.compose.ui.graphics.Color(0xFF535F70),
    onSecondary = androidx.compose.ui.graphics.Color.White,
    outline = androidx.compose.ui.graphics.Color(0xFF74777F)
)
