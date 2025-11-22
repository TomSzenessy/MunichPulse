package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun GoogleLoginButton(
    modifier: Modifier,
    text: String,
    onLoginSuccess: (String, String?) -> Unit,
    onLoginError: (String) -> Unit
) {
    Button(
        onClick = { 
            // TODO: Implement Google Sign In for Web
            onLoginSuccess("dummy_token", "Web User") 
        },
        modifier = modifier
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Text(text)
    }
}
