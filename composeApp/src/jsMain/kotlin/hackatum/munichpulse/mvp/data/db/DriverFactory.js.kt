package hackatum.munichpulse.mvp.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DriverFactory actual constructor(context: PlatformContext) {
    actual fun createDriver(): SqlDriver {
        val worker = Worker(js("""new URL("sqljs.worker.js", import.meta.url)""") as String)
        return WebWorkerDriver(worker)
    }
}
