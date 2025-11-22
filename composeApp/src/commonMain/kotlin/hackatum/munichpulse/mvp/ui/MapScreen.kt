package hackatum.munichpulse.mvp.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun MapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Map Image
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCgMGykCQJc76lxR3xwrkfmW2Wh9o-q4H854fQlHptaGbvqR9_X9ZlAL0Q4DMKj7SGfHZxsKKqmpXiRAWxln6rI2aDL8TtqATOsGBSjuFjMtOlFf7M6VswXKYtQbyYvplXY17tWXH1c1gAz2GH9C9kl1UaKkfOMm1U4-0eBBAWIMFUjlL8qnj3sUv2p62Fd81wYVMlHjyeuMV7-oHQOnBRSS46nT3E367yldCVg6EsmzaB2NRe5HaAp3xm6SHBAFS9oVXpvp3WJcN48",
            contentDescription = "Map Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Search Bar
        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            SearchBar()
        }

        // Zoom Controls
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .padding(bottom = 100.dp) // Adjust for bottom nav and waveform
        ) {
            ZoomControls()
            Spacer(modifier = Modifier.height(12.dp))
            NavigationButton()
        }

        // Waveform Animation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // Space for bottom nav
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            WaveformAnimation()
        }
    }
}

@Composable
fun ZoomControls() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
    ) {
        IconButton(onClick = { /* Zoom In */ }) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimary)
        }
        Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Black.copy(alpha = 0.1f)))
        IconButton(onClick = { /* Zoom Out */ }) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimary)
        }
    }
}

@Composable
fun NavigationButton() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Navigation,
            contentDescription = "Navigate",
            tint = TextPrimary,
            modifier = Modifier.rotate(45f) // Adjust rotation to match design
        )
    }
}

@Composable
fun WaveformAnimation() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(40.dp)
    ) {
        repeat(12) { index ->
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, delayMillis = index * 100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(scale)
                    .background(PrimaryGreen, CircleShape)
            )
        }
    }
}
