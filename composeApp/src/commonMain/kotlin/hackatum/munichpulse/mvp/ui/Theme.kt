package hackatum.munichpulse.mvp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import hackatum.munichpulse.mvp.domain.SettingsRepository

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
    content: @Composable () -> Unit
) {
    val isDarkMode by hackatum.munichpulse.mvp.domain.SettingsRepository.isDarkMode.collectAsState(initial = true)
    
    // For now, we only have a Dark Color Scheme defined. 
    // If Light Mode is requested, we should define a LightColorScheme.
    // For this MVP step, I'll define a basic LightColorScheme fallback or just toggle colors.
    
    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
    onBackground = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF121212),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF757575),
    secondary = androidx.compose.ui.graphics.Color(0xFF757575),
    onSecondary = androidx.compose.ui.graphics.Color.White,
)
