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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hackatum.munichpulse.mvp.ui.LocalAppStrings
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen
import hackatum.munichpulse.mvp.ui.components.MapView

import hackatum.munichpulse.mvp.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel { MapViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 800.dp

        // Map View (Full Screen)
        MapView(
            modifier = Modifier.fillMaxSize()
        )

        if (isWideScreen) {
            // Desktop Layout: Sidebar (Floating)
            Box(modifier = Modifier.fillMaxSize()) {
                // Sidebar
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(24.dp)
                        .align(Alignment.TopStart),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            strings.mapTab,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SearchBar(
                            modifier = Modifier.fillMaxWidth(),
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("Filter Events", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Interactive Filters
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = uiState.selectedFilter == "All", onClick = { viewModel.setFilter("All") }, label = { Text("All") })
                            FilterChip(selected = uiState.selectedFilter == "Music", onClick = { viewModel.setFilter("Music") }, label = { Text("Music") })
                            FilterChip(selected = uiState.selectedFilter == "Food", onClick = { viewModel.setFilter("Food") }, label = { Text("Food") })
                        }
                    }
                }
            }
            
            // Zoom Controls (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                ZoomControls()
            }
            
        } else {
            // Mobile Layout
            
            // Search Bar (Top)
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) }
                )
            }

            // Zoom Controls (Right)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .padding(bottom = 100.dp)
            ) {
                ZoomControls()
                Spacer(modifier = Modifier.height(12.dp))
                NavigationButton()
            }

            // Waveform Animation (Bottom) - Optimized: Only show if needed, maybe simplified
            // User reported lag, so let's make it lighter or remove if not essential.
            // Keeping it simple for now but ensuring it doesn't block main thread.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                WaveformAnimation()
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, query: String = "", onQueryChange: (String) -> Unit = {}) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = modifier
            .height(48.dp)
            .then(if (modifier == Modifier) Modifier.width(300.dp) else Modifier), // Default width for mobile
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            // Simple Text for now, but could be BasicTextField
            Text(if (query.isEmpty()) strings.searchPlaceholder else query, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ZoomControls() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        IconButton(onClick = { /* Zoom In */ }) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
        }
        Box(modifier = Modifier.height(1.dp).width(40.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        IconButton(onClick = { /* Zoom Out */ }) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun NavigationButton() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Navigation,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.rotate(45f)
        )
    }
}

@Composable
fun WaveformAnimation() {
    // Reduced count for performance
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(40.dp)
    ) {
        repeat(8) { index -> // Reduced from 12 to 8
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
