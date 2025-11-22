package hackatum.munichpulse.mvp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    text: String,
    onLoginSuccess: (String, String?) -> Unit, // idToken, name
    onLoginError: (String) -> Unit
)
