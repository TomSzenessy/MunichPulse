package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import hackatum.munichpulse.mvp.data.repository.SettingsRepository
import hackatum.munichpulse.mvp.ui.theme.PrimaryGreen
import hackatum.munichpulse.mvp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel { LoginViewModel() },
    onLoginSuccess: (String, Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var loginError by remember { mutableStateOf<String?>(null) }

    // --- Firebase / Google Setup ---
    // Handled by GoogleLoginButton


    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isWideScreen = maxWidth > 800.dp

        // Background Image logic (Keep existing)
        if (!isWideScreen) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=2070&auto=format&fit=crop",
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.3f)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        } else {
            // Desktop Split Layout
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=2070&auto=format&fit=crop",
                        contentDescription = "Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = strings.appName, style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                        Text(text = strings.slogan, style = MaterialTheme.typography.headlineSmall.copy(color = Color.White.copy(alpha = 0.8f)), modifier = Modifier.padding(top = 16.dp))
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.background))
            }
        }

        // Content Container
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = if (isWideScreen) Alignment.CenterEnd else Alignment.Center
        ) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) { LanguageToggle() }

            Card(
                modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth().then(if (isWideScreen) Modifier.padding(end = 80.dp) else Modifier),
                colors = CardDefaults.cardColors(containerColor = if (isWideScreen) MaterialTheme.colorScheme.surface else Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isWideScreen) 8.dp else 0.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    // Header Text
                    if (!isWideScreen) {
                        Text(text = strings.appName, style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryGreen))
                        Text(text = strings.slogan, style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)), modifier = Modifier.padding(top = 8.dp, bottom = 48.dp))
                    } else {
                        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface), modifier = Modifier.padding(bottom = 32.dp))
                    }

                    // 1. Enter Name
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text(strings.loginNameLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. Local / Newby Toggle
                    LocalNewbyToggle(isLocal = uiState.isLocal, onToggle = { viewModel.toggleLocal(it) }, strings = strings)

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Sign in with Google (VISUAL UPDATE)
                    GoogleLoginButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = strings.loginGoogle,
                        onLoginSuccess = { idToken, name ->
                            val finalName = name ?: "Google User"
                            viewModel.updateName(finalName)
                            viewModel.loginWithGoogle(idToken, finalName, uiState.isLocal) { success ->
                                if (success) onLoginSuccess(finalName, uiState.isLocal)
                            }
                        },
                        onLoginError = { error ->
                            // Surface the error visibly on screen for Web users
                            loginError = error
                            println("Login Error: $error")
                        }
                    )

                    if (loginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = loginError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Divider
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Text(text = strings.orDivider, modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Continue as Guest
                    OutlinedButton(
                        onClick = {
                            viewModel.login("Guest", false) { success ->
                                if (success) onLoginSuccess("Guest", false)
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = strings.loginGuest, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalNewbyToggle(isLocal: Boolean, onToggle: (Boolean) -> Unit, strings: AppStrings) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Helper for Toggle Option
            val toggleOption = @Composable { selected: Boolean, text: String, onClick: () -> Unit ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selected) PrimaryGreen else Color.Transparent)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            
            toggleOption(isLocal, strings.loginLocalLabel) { onToggle(true) }
            toggleOption(!isLocal, strings.loginNewbyLabel) { onToggle(false) }
        }
    }
}

// --- GOOGLE LOGO DRAWING (No Assets needed) ---
@Composable
fun GoogleLogoSvg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Google Blue
        drawPath(
            path = Path().apply {
                moveTo(w * 0.99f, h * 0.5f)
                lineTo(w * 0.99f, h * 0.5f)
                cubicTo(w * 0.99f, h * 0.42f, w * 0.98f, h * 0.35f, w * 0.96f, h * 0.28f)
                lineTo(w * 0.51f, h * 0.28f)
                lineTo(w * 0.51f, h * 0.47f)
                lineTo(w * 0.78f, h * 0.47f)
                cubicTo(w * 0.77f, h * 0.55f, w * 0.73f, h * 0.62f, w * 0.68f, h * 0.66f)
                lineTo(w * 0.68f, h * 0.82f)
                lineTo(w * 0.84f, h * 0.82f)
                cubicTo(w * 0.93f, h * 0.73f, w * 0.99f, h * 0.61f, w * 0.99f, h * 0.47f) // adjusted
            },
            color = Color(0xFF4285F4),
            style = Fill
        )
        
        // Google Green
        drawPath(
            path = Path().apply {
                moveTo(w * 0.51f, h * 0.96f)
                cubicTo(w * 0.64f, h * 0.96f, w * 0.75f, h * 0.92f, w * 0.84f, h * 0.84f) // adjusted
                lineTo(w * 0.68f, h * 0.68f) // adjusted
                cubicTo(w * 0.64f, h * 0.71f, w * 0.58f, h * 0.73f, w * 0.51f, h * 0.73f)
                cubicTo(w * 0.38f, h * 0.73f, w * 0.27f, h * 0.65f, w * 0.23f, h * 0.54f)
                lineTo(w * 0.07f, h * 0.66f)
                lineTo(w * 0.07f, h * 0.66f)
                cubicTo(w * 0.15f, h * 0.83f, w * 0.32f, h * 0.96f, w * 0.51f, h * 0.96f)
            },
            color = Color(0xFF34A853),
            style = Fill
        )
        
        // Google Yellow
        drawPath(
            path = Path().apply {
                moveTo(w * 0.23f, h * 0.54f)
                cubicTo(w * 0.21f, h * 0.5f, w * 0.2f, h * 0.46f, w * 0.2f, h * 0.42f) // adjusted
                cubicTo(w * 0.2f, h * 0.38f, w * 0.21f, h * 0.34f, w * 0.23f, h * 0.3f)
                lineTo(w * 0.07f, h * 0.17f)
                lineTo(w * 0.06f, h * 0.17f)
                cubicTo(w * 0.02f, h * 0.26f, w * 0f, h * 0.34f, w * 0f, h * 0.42f)
                cubicTo(w * 0f, h * 0.51f, w * 0.02f, h * 0.59f, w * 0.07f, h * 0.67f) // adjusted
                lineTo(w * 0.23f, h * 0.54f)
            },
            color = Color(0xFFFBBC05),
            style = Fill
        )
        
        // Google Red
        drawPath(
            path = Path().apply {
                moveTo(w * 0.51f, h * 0.11f)
                cubicTo(w * 0.58f, h * 0.11f, w * 0.64f, h * 0.13f, w * 0.69f, h * 0.18f)
                lineTo(w * 0.85f, h * 0.02f)
                cubicTo(w * 0.75f, -0.07f, w * 0.64f, -0.1f, w * 0.51f, -0.1f) // adjusted bound
                cubicTo(w * 0.32f, -0.1f, w * 0.15f, 0.03f, w * 0.07f, 0.19f) // adjusted
                lineTo(w * 0.23f, 0.32f)
                cubicTo(w * 0.27f, 0.21f, w * 0.38f, 0.13f, w * 0.51f, 0.13f)
                lineTo(w * 0.51f, 0.11f)
            },
            color = Color(0xFFEA4335),
            style = Fill
        )
    }
}

@Composable
fun LanguageToggle() {
    val scope = rememberCoroutineScope()
    val language by SettingsRepository.language.collectAsState()
    val strings = LocalAppStrings.current

    Surface(
        onClick = {
            scope.launch {
                val newLang = if (language == "English") "German" else "English"
                SettingsRepository.setLanguage(newLang)
            }
        },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Language, contentDescription = "Language", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            val isDark = isSystemInDarkTheme()
            Text(
                strings.languageButton,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) Color.White else Color.Black
            )
        }
    }
}

