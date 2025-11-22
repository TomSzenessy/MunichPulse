package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.domain.LogbookEntry
import hackatum.munichpulse.mvp.domain.MockUserRepository

@Composable
fun ProfileScreen() {
    val repository = remember { MockUserRepository() }
    val user by repository.getCurrentUser().collectAsState(initial = null)
    val logbookEntries by repository.getLogbookEntries().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = 80.dp)
    ) {
        item {
            ProfileHeader(user?.name ?: "Loading...", user?.avatarUrl)
        }
        item {
            Text(
                "Logbook",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(logbookEntries) { entry ->
            LogbookItem(entry)
        }
    }
}

@Composable
fun ProfileHeader(name: String, avatarUrl: String?) {
    Column(modifier = Modifier.fillMaxWidth().background(DarkBackground)) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Back */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(
                "Profile",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.size(48.dp)) // Balance back button
        }

        // Avatar and Stats
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = avatarUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuDQts8JbmFxIyYdug0PKmZl5OdhBLP9hTKxz_uKAnvZpuac-30tETuTBqRdPE5DEPk5hGdiNUmR-lJ0TMYJd9x8vnY8MAnOUuRtk_Jxmnq_35gj4zrpxZBXXxQRWPEfbXQjl2HAwfxM3A7w_cffrbD-w42pStMjw3Xy16IVJblj48904FPZMfxkyduNsXQBWhVSAVdLq_SbrEQbfJZJCXmXbYCk6CZCMyZ6-vXnpoR1nrxlPFW3EaC_KF9HD_GeA6bx_ua7c20OJft0",
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(128.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Callsign: $name", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Local", color = TextSecondary, fontSize = 16.sp)
            Text("Joined 2022", color = TextSecondary, fontSize = 16.sp)
        }
    }
}

@Composable
fun LogbookItem(entry: LogbookEntry) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        // Timeline Line
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextPrimary)
            Box(modifier = Modifier.width(2.dp).height(40.dp).background(DarkBorder))
        }
        
        // Content
        Column(modifier = Modifier.padding(bottom = 24.dp).weight(1f)) {
            Text(entry.locationName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(
                "Distance Traveled: ${entry.distanceTraveled} km | Crowd Contribution: ${entry.crowdContribution}",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}
