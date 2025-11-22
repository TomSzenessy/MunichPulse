package hackatum.munichpulse.mvp.data.db

import android.content.Context

actual sealed class PlatformContext

class AndroidPlatformContext(val context: Context) : PlatformContext()
