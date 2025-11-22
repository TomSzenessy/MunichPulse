package hackatum.munichpulse.mvp.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory for creating SQLDelight drivers.
 * Implementation varies by platform (Android, iOS, Desktop).
 */
expect class DriverFactory(context: PlatformContext) {
    /**
     * Creates a SQLDelight driver for the current platform.
     */
    fun createDriver(): SqlDriver
}
