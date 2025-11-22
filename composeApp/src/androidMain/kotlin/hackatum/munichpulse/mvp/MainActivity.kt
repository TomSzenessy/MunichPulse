package hackatum.munichpulse.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mapbox.common.MapboxOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken = BuildConfig.MAPBOX_PUBLIC_TOKEN

        setContent {
            App()
        }
    }
}
