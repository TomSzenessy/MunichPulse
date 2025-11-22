package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.data.model.Group
import hackatum.munichpulse.mvp.data.model.ChatMessage
import hackatum.munichpulse.mvp.data.repository.GroupRepository
import hackatum.munichpulse.mvp.data.model.User
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen
import hackatum.munichpulse.mvp.ui.theme.DarkBackground

import hackatum.munichpulse.mvp.viewmodel.GroupViewModel

@Composable
fun GroupScreen(
    viewModel: GroupViewModel = androidx.lifecycle.viewmodel.compose.viewModel { GroupViewModel() }
) {
    val groups by viewModel.groups.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    // Update selected group when groups change (to reflect new messages)
    val currentSelectedGroup = groups.find { it.id == uiState.selectedGroup?.id } ?: uiState.selectedGroup

    if (currentSelectedGroup != null) {
        ChatView(
            group = currentSelectedGroup,
            onBack = { viewModel.selectGroup(null) },
            onSendMessage = { text ->
                // Mock current user
                val currentUser = User("me", "Me", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true)
                viewModel.sendMessage(text, currentUser)
            }
        )
    } else {
        GroupList(groups = groups, onGroupClick = { viewModel.selectGroup(it) })
    }
}

@Composable
fun GroupList(groups: List<Group>, onGroupClick: (Group) -> Unit) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Header()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(groups) { group ->
                GroupCard(group, onClick = { onGroupClick(group) })
            }
        }
    }
}

@Composable
private fun Header() {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(strings.squadsTab, color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassIconButton(Icons.Default.Search, "Search")
            GlassIconButton(Icons.Default.Add, "Add")
        }
    }
}

@Composable
private fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun GroupCard(
    group: Group,
    titleOverride: String? = null,
    statusOverride: String? = null,
    statusColorOverride: Color? = null,
    onClick: () -> Unit
) {
    // Mock data mapping for display
    val title = titleOverride ?: if (group.eventId == "1") "Tollwood Summer Festival" else if (group.eventId == "3") "Open Air Kino" else "Unknown Event"
    val status = statusOverride ?: "Live now"
    val statusColor = statusColorOverride ?: PrimaryGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
             val avatars = group.members.map { it.avatarUrl }.take(3)
             
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(group: Group, onBack: () -> Unit, onSendMessage: (String) -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val title = if (group.eventId == "1") "Tollwood Summer Festival" else if (group.eventId == "3") "Open Air Kino" else "Group Chat"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(PrimaryGreen, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(group.chatMessages) { message ->
                val isMe = message.senderId == "me"
                ChatBubble(message, isMe)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 0.dp,
                    bottomEnd = if (isMe) 0.dp else 16.dp
                ))
                .background(if (isMe) PrimaryGreen else MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        }
    }
}
