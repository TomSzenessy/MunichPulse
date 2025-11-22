package hackatum.munichpulse.mvp.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory actual constructor(private val platformContext: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        val context = (platformContext as AndroidPlatformContext).context
        return AndroidSqliteDriver(MunichPulseDatabase.Schema, context, "MunichPulseDatabase.db")
    }
}