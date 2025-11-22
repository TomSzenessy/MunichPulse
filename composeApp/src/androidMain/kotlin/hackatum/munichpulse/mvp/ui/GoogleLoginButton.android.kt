package hackatum.munichpulse.mvp.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
actual fun GoogleLoginButton(
    modifier: Modifier,
    text: String,
    onLoginSuccess: (String, String?) -> Unit,
    onLoginError: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Configure Google Sign In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID_FROM_FIREBASE_CONSOLE") // TODO: Add your Web Client ID here
            .requestEmail()
            .build()
    }

    // 2. Create the Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(Exception::class.java)
                val idToken = account.idToken
                val name = account.displayName
                
                if (idToken != null) {
                    // Pass the ID token back to common code
                    onLoginSuccess(idToken, name)
                } else {
                    onLoginError("Google Sign In Failed: No ID Token")
                }
            } catch (e: Exception) {
                onLoginError("Google Sign In Failed: ${e.localizedMessage}")
            }
        }
    }

    Button(
        onClick = { googleSignInLauncher.launch(GoogleSignIn.getClient(context, gso).signInIntent) },
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
