package hackatum.munichpulse.mvp.data.db

import hackatum.munichpulse.mvp.data.db.MunichPulseDatabase

actual fun getDatabase(context: PlatformContext): MunichPulseDatabase {
    val driver = DriverFactory(context).createDriver()
    return MunichPulseDatabase(driver)
}