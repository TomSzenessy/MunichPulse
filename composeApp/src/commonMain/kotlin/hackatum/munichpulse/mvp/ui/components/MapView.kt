package hackatum.munichpulse.mvp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapView(
    modifier: Modifier = Modifier,
    latitude: Double = 48.1351, // Munich default
    longitude: Double = 11.5820,
    zoom: Double = 12.0
)
