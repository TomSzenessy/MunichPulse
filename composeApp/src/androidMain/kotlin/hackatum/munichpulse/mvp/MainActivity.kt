package hackatum.munichpulse.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hackatum.munichpulse.mvp.data.db.getDatabase
import hackatum.munichpulse.mvp.data.db.AndroidPlatformContext
import hackatum.munichpulse.mvp.ui.LoginScreen
import hackatum.munichpulse.mvp.ui.ProvideAppStrings
import hackatum.munichpulse.mvp.ui.theme.UrbanPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val database = getDatabase(AndroidPlatformContext(applicationContext))

        setContent {
            App(database)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    ProvideAppStrings {
        UrbanPulseTheme {
            LoginScreen(onLoginSuccess = { _, _ -> })
        }
    }
}
