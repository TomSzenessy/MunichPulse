package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.domain.Group
import hackatum.munichpulse.mvp.domain.MockGroupRepository

@Composable
fun GroupScreen() {
    val repository = remember { MockGroupRepository() }
    val groups by repository.getMyGroups().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Header()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(groups) { group ->
                GroupCard(group)
            }
            // Add some dummy items to match design variety if needed
             item {
                GroupCard(
                    Group("3", "2", emptyList()), // Dummy group for "Open Air Kino"
                    titleOverride = "Open Air Kino",
                    statusOverride = "Event starts in 2h",
                    statusColorOverride = TextSecondary
                )
            }
             item {
                GroupCard(
                    Group("4", "4", emptyList()), // Dummy group for "Haidhausen..."
                    titleOverride = "Haidhausen French Quarter Festival",
                    statusOverride = "Event tomorrow",
                    statusColorOverride = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Squads", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassIconButton(Icons.Default.Search, "Search")
            GlassIconButton(Icons.Default.Add, "Add")
        }
    }
}

@Composable
private fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(DarkSurface.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = TextSecondary)
    }
}

@Composable
fun GroupCard(
    group: Group,
    titleOverride: String? = null,
    statusOverride: String? = null,
    statusColorOverride: Color? = null
) {
    // Mock data mapping for display
    val title = titleOverride ?: if (group.eventId == "1") "Tollwood Summer Festival" else "Unknown Event"
    val status = statusOverride ?: "Live now"
    val statusColor = statusColorOverride ?: PrimaryGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                if (statusColor == PrimaryGreen) {
                    Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(status, color = statusColor, fontSize = 14.sp)
            }
        }
        
        // Overlapping Avatars
        Box(modifier = Modifier.width(80.dp).height(40.dp)) {
             // Mock avatars for visual fidelity to design
             val avatars = listOf(
                 "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR",
                 "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR",
                 "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR"
             )
             
             avatars.forEachIndexed { index, url ->
                 AsyncImage(
                     model = url,
                     contentDescription = null,
                     modifier = Modifier
                         .align(Alignment.CenterEnd)
                         .padding(end = (index * 24).dp) // Overlap effect
                         .size(40.dp)
                         .clip(CircleShape)
                         .border(2.dp, DarkBackground, CircleShape),
                     contentScale = ContentScale.Crop
                 )
             }
        }
    }
}
