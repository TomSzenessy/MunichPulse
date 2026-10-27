package hackatum.munichpulse.mvp

import android.app.Application
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

class MunichPulseApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Analytics stay off when POSTHOG_API_KEY or POSTHOG_HOST is missing from local.properties
        if (BuildConfig.POSTHOG_API_KEY.isNotEmpty() && BuildConfig.POSTHOG_HOST.isNotEmpty()) {
            val config = PostHogAndroidConfig(
                apiKey = BuildConfig.POSTHOG_API_KEY,
                host = BuildConfig.POSTHOG_HOST
            ).apply {
                captureScreenViews = true
            }
            PostHogAndroid.setup(this, config)
        }
    }
}
