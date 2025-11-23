package hackatum.munichpulse.mvp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.browser.document
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import hackatum.munichpulse.mvp.js.Secrets
import kotlinx.coroutines.launch
import kotlinx.browser.window

@Composable
actual fun GoogleLoginButton(
    modifier: Modifier,
    text: String,
    onLoginSuccess: (String, String?) -> Unit,
    onLoginError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            // Google Sign-In for Web using Google Identity Services (GIS)
            try {
                // Prefer build-time injected client ID from Secrets; if blank, fall back to meta tag.
                val secretClientId = Secrets.GOOGLE_WEB_CLIENT_ID
                val metaClientId = try {
                    val meta = document.querySelector("meta[name=\"google-signin-client_id\"]")
                    meta?.getAttribute("content") ?: ""
                } catch (_: Throwable) { "" }
                val clientId = (if (secretClientId.isNotBlank()) secretClientId else metaClientId)
                if (clientId.isBlank()) {
                    onLoginError("Missing GOOGLE_WEB_CLIENT_ID. Define it in local.properties (GOOGLE_WEB_CLIENT_ID=your-client-id), or set <meta name=\"google-signin-client_id\" content=\"...\" /> in index.html, then do a Clean Rebuild.")
                    return@Button
                }

                fun startGisFlow() {
                    var consumed = false
                    val cfg = js("({})")
                    cfg.client_id = clientId
                    cfg.callback = fun(response: dynamic) {
                        if (consumed) return
                        consumed = true
                        val idToken = (response?.credential as? String)
                        if (idToken == null) {
                            onLoginError("Google Sign-In failed: No credential returned")
                        } else {
                            scope.launch {
                                try {
                                    FirebaseInterface.getInstance().signInWithGoogle(idToken)
                                    val name = Firebase.auth.currentUser?.displayName
                                    onLoginSuccess(idToken, name)
                                } catch (t: Throwable) {
                                    onLoginError("Firebase sign-in failed: ${t.message}")
                                }
                            }
                        }
                    }

                    val googleDyn2 = js("window.google")
                    if (googleDyn2 == undefined) {
                        onLoginError("Google Identity Services unavailable after load attempt")
                        return
                    }
                    googleDyn2.accounts.id.initialize(cfg)
                    googleDyn2.accounts.id.prompt()
                }

                val googleDyn = js("window.google")
                if (googleDyn == undefined) {
                    // Dynamically load GIS script if it's not present or index.html isn't used
                    val head = document.getElementsByTagName("head").item(0)
                    val script = document.createElement("script")
                    script.setAttribute("src", "https://accounts.google.com/gsi/client")
                    script.setAttribute("async", "true")
                    script.setAttribute("defer", "true")
                    script.asDynamic().onload = { startGisFlow() }
                    script.asDynamic().onerror = {
                        onLoginError("Failed to load Google Identity Services script from https://accounts.google.com/gsi/client")
                    }
                    head?.appendChild(script)
                } else {
                    startGisFlow()
                }
            } catch (t: Throwable) {
                onLoginError("Google Sign-In error: ${t.message}")
            }
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
