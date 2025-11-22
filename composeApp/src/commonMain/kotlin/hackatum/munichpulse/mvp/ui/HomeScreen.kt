package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.domain.Event
import hackatum.munichpulse.mvp.domain.MockEventRepository

@Composable
fun HomeScreen() {
    val repository = remember { MockEventRepository() }
    val trendingEvents by repository.getTrendingEvents().collectAsState(initial = emptyList())
    val nearbyEvents by repository.getNearbyEvents().collectAsState(initial = emptyList())
    val allEvents by repository.getAllEvents().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = 80.dp) // Space for bottom nav
    ) {
        item {
            SearchBar()
        }

        item {
            SectionHeader("Trending Now")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trendingEvents) { event ->
                    TrendingEventCard(event)
                }
            }
        }

        item {
            SectionHeader("Near You")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(nearbyEvents) { event ->
                    NearbyEventCard(event)
                }
            }
        }

        item {
            SectionHeader("Discover New Events")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                allEvents.forEach { event ->
                    DiscoverEventCard(event)
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    Box(modifier = Modifier.padding(16.dp).padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon would go here, using text for now or vector if available
            Text("🔍", color = TextSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search for events or places", color = TextSecondary)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun TrendingEventCard(event: Event) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
    ) {
        Box(modifier = Modifier.height(160.dp)) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(event.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(event.location, color = TextSecondary, fontSize = 14.sp)
                FullnessIndicator(event.fullnessPercentage)
            }
        }
    }
}

@Composable
fun NearbyEventCard(event: Event) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = event.imageUrl,
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.height(112.dp).fillMaxWidth()
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(event.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text(event.location, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(modifier = Modifier.height(8.dp))
            FullnessIndicator(event.fullnessPercentage, small = true)
        }
    }
}

@Composable
fun DiscoverEventCard(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = event.imageUrl,
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(event.location, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(modifier = Modifier.height(8.dp))
            FullnessIndicator(event.fullnessPercentage)
        }
    }
}

@Composable
fun FullnessIndicator(percentage: Int, small: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val color = when {
            percentage >= 90 -> RedStatus
            percentage >= 50 -> YellowStatus
            else -> PrimaryGreen
        }
        Box(
            modifier = Modifier
                .size(if (small) 12.dp else 16.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(if (small) 4.dp else 6.dp))
        Text(
            "$percentage%",
            color = TextPrimary,
            fontSize = if (small) 12.sp else 14.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
        Text(
            " full",
            color = TextSecondary,
            fontSize = if (small) 12.sp else 14.sp
        )
    }
}
