package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import munichpulse.composeapp.generated.resources.Res
import munichpulse.composeapp.generated.resources.icon

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.icon),
            contentDescription = "App Logo",
            modifier = Modifier.size(128.dp)
        )
    }
}
