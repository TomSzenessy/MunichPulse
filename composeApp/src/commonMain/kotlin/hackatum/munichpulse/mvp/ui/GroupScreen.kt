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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
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
import hackatum.munichpulse.mvp.domain.Group
import hackatum.munichpulse.mvp.domain.GroupRepository
import hackatum.munichpulse.mvp.domain.User

@Composable
fun GroupScreen() {
    val groups by GroupRepository.getMyGroups().collectAsState(initial = emptyList())
    var selectedGroup by remember { mutableStateOf<Group?>(null) }

    // Update selected group when groups change (to reflect new messages)
    val currentSelectedGroup = groups.find { it.id == selectedGroup?.id } ?: selectedGroup

    if (currentSelectedGroup != null) {
        ChatView(
            group = currentSelectedGroup,
            onBack = { selectedGroup = null },
            onSendMessage = { text ->
                // Mock current user
                val currentUser = User("me", "Me", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true)
                GroupRepository.sendMessage(currentSelectedGroup.id, text, currentUser)
            }
        )
    } else {
        GroupList(groups = groups, onGroupClick = { selectedGroup = it })
    }
}

@Composable
fun GroupList(groups: List<Group>, onGroupClick: (Group) -> Unit) {
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
                GroupCard(group, onClick = { onGroupClick(group) })
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
private fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(DarkSurface.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable(onClick = onClick),
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
            .background(DarkSurface.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
                title = { Text(title, color = TextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...", color = TextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
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
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkBackground)
                }
            }
        },
        containerColor = DarkBackground
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
fun ChatBubble(message: hackatum.munichpulse.mvp.domain.ChatMessage, isMe: Boolean) {
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
                .background(if (isMe) PrimaryGreen else DarkSurface)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isMe) DarkBackground else TextPrimary,
                fontSize = 16.sp
            )
        }
    }
}
