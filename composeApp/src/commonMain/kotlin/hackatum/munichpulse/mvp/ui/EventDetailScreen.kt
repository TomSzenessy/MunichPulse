package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import hackatum.munichpulse.mvp.viewmodel.EventDetailViewModel
import androidx.compose.material3.ButtonDefaults
import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onOpenGroup: (String) -> Unit,
    viewModel: EventDetailViewModel = viewModel { EventDetailViewModel() }
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val event by viewModel.event.collectAsState()
    val currentGroup by viewModel.currentGroupForEvent.collectAsState()
    val strings = LocalAppStrings.current

    // Only redirect when explicitly requested (e.g. after clicking join)
    val shouldRedirect = remember { mutableStateOf(false) }
    LaunchedEffect(currentGroup) {
        if (shouldRedirect.value && currentGroup != null) {
            onOpenGroup(currentGroup!!.id)
            shouldRedirect.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            // Join/Open/Leave buttons fixed at the bottom
            Surface(color = MaterialTheme.colorScheme.background) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val openJoinDialog = remember { mutableStateOf(false) }
                    val openLeaveDialog = remember { mutableStateOf(false) }

                    // Expose the dialogs when needed
                    if (openJoinDialog.value) {
                        AlertDialog(
                            onDismissRequest = { openJoinDialog.value = false },
                            title = { Text(strings.joinGroup) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        "How would you like to join?",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    // Emphasize joining a random group as the primary action
                                    Button(
                                        onClick = {
                                            openJoinDialog.value = false
                                            shouldRedirect.value = true
                                            viewModel.addUserToEventGroup(eventId)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Join Random Group of 5") }
                                    // Solo join as secondary/outlined option
                                    OutlinedButton(
                                        onClick = {
                                            openJoinDialog.value = false
                                            shouldRedirect.value = true
                                            viewModel.addUserToIndividualEventGroup(eventId)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("Join Solo") }
                                }
                            },
                            confirmButton = {},
                            dismissButton = {
                                TextButton(onClick = { openJoinDialog.value = false }) {
                                    Text(strings.cancel)
                                }
                            }
                        )
                    }
                    
                    if (openLeaveDialog.value) {
                        AlertDialog(
                            onDismissRequest = { openLeaveDialog.value = false },
                            title = { Text(strings.confirmLeaveGroupTitle) },
                            text = { Text(strings.confirmLeaveGroupText) },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        openLeaveDialog.value = false
                                        currentGroup?.let { viewModel.leaveGroup(eventId, it.id) }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(strings.confirm)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { openJoinDialog.value = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }


                    if (currentGroup == null) {
                        Button(
                            onClick = { openJoinDialog.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Join Event")
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { currentGroup?.let { onOpenGroup(it.id) } },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open Group")
                            }
                            OutlinedButton(
                                onClick = { openLeaveDialog.value = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Leave Group")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            event?.let { currentEvent ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                        AsyncImage(
                            model = currentEvent.imageUrl,
                            contentDescription = currentEvent.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                        startY = 300f
                                    )
                                )
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-32).dp)) {
                        Text(
                            text = currentEvent.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentEvent.location,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Experience the best of Munich at ${currentEvent.title}. Join us for an unforgettable time at ${currentEvent.location}. This event features amazing activities and a great atmosphere.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Fullness indicator
                        Text(
                            text = "Current Capacity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { currentEvent.fullnessPercentage / 100f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Text(
                            text = "${currentEvent.fullnessPercentage}% Full",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
