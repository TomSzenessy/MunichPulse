package hackatum.munichpulse.mvp.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory actual constructor(private val context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        val androidContext = (context as AndroidPlatformContext).context
        return AndroidSqliteDriver(MunichPulseDatabase.Schema, androidContext, "MunichPulseDatabase.db")
    }
}