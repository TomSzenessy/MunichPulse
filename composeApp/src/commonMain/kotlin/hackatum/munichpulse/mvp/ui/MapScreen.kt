package hackatum.munichpulse.mvp.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hackatum.munichpulse.mvp.ui.LocalAppStrings
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen
import hackatum.munichpulse.mvp.viewmodel.MapViewModel
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel { MapViewModel() },
    onNavigateToEvent: (String) -> Unit = {} // Callback for navigation
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    
    val mapController = rememberMapController()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 800.dp

        // --- MAP VIEW IMPLEMENTATION ---
        MapComponent(
            modifier = Modifier.fillMaxSize(),
            mapController = mapController,
            userLocation = uiState.userLocation,
            otherPeople = uiState.otherPeople,
            events = uiState.events,
            selectedFilter = uiState.selectedFilter,
            onNavigateToEvent = onNavigateToEvent
        )
        // -------------------------------

        if (isWideScreen) {
            // Desktop Sidebar
            Box(modifier = Modifier.fillMaxSize()) {
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
                // Pass actions to zoom controls
                ZoomControls(
                    onZoomIn = { mapController.zoomIn() },
                    onZoomOut = { mapController.zoomOut() }
                )
            }
            
        } else {
            // Mobile Layout
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .padding(bottom = 100.dp)
            ) {
                ZoomControls(
                    onZoomIn = { mapController.zoomIn() },
                    onZoomOut = { mapController.zoomOut() }
                )
                Spacer(modifier = Modifier.height(12.dp))
                NavigationButton(onClick = {
                    // Reset camera to user location
                    mapController.recenter(uiState.userLocation)
                })
            }

        }
    }
}

// --- HELPER TO CREATE DOTS (Replaces Drawables) ---
// Moved to Android implementation


@Composable
fun SearchBar(modifier: Modifier = Modifier, query: String = "", onQueryChange: (String) -> Unit = {}) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = modifier
            .height(48.dp)
            .then(if (modifier == Modifier) Modifier.width(300.dp) else Modifier),
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
            Text(if (query.isEmpty()) strings.searchPlaceholder else query, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Updated ZoomControls to accept callbacks
@Composable
fun ZoomControls(onZoomIn: () -> Unit, onZoomOut: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        IconButton(onClick = onZoomIn) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
        }
        Box(modifier = Modifier.height(1.dp).width(40.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
        IconButton(onClick = onZoomOut) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Updated Navigation Button to handle click
@Composable
fun NavigationButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Navigation,
            contentDescription = "Recenter",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.rotate(45f)
        )
    }
}