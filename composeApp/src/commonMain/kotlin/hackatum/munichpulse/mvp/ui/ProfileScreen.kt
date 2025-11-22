package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen

import hackatum.munichpulse.mvp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel { ProfileViewModel() }
) {
    val user by viewModel.user.collectAsState()
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 80.dp)
    ) {
        item {
            ProfileHeader(
                name = user?.name ?: "Loading...",
                avatarUrl = user?.avatarUrl,
                isLocal = user?.isLocal == true
            )
        }

        item {
            SettingsSection(viewModel)
        }
    }
}

@Composable
fun SettingsSection(viewModel: ProfileViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val strings = LocalAppStrings.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            strings.settingsTitle,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dark Mode Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.darkModeLabel, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.setDarkMode(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    checkedTrackColor = PrimaryGreen,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.languageLabel, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(language, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                Switch(
                    checked = language == "German",
                    onCheckedChange = { 
                        viewModel.setLanguage(if (it) "German" else "English") 
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.surface,
                        checkedTrackColor = PrimaryGreen,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, avatarUrl: String?, isLocal: Boolean) {
    val strings = LocalAppStrings.current
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Back */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                strings.profileTab,
                color = MaterialTheme.colorScheme.onBackground,
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
            Text("Callsign: $name", color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(if (isLocal) "Local" else "Online", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
        }
    }
}
