package hackatum.munichpulse.mvp

import android.app.Application
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

class MunichPulseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.POSTHOG_API_KEY.isNotEmpty() && BuildConfig.POSTHOG_HOST.isNotEmpty()) {
            val config = PostHogAndroidConfig(
                apiKey = BuildConfig.POSTHOG_API_KEY,
                host = BuildConfig.POSTHOG_HOST
            )
            PostHogAndroid.setup(this, config)
        }
    }
}
