package hackatum.munichpulse.mvp

import android.annotation.SuppressLint
import android.content.Context
import hackatum.munichpulse.mvp.backend.GpsTracker
import hackatum.munichpulse.mvp.model.Location

class AndroidContextHolder(private val context: Context) {

    private fun get(): Context = this.context

    var gpsTracker: GpsTracker? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var contextHolder: AndroidContextHolder? = null

        fun initContextHolder(context: Context) {
            contextHolder = AndroidContextHolder(context)
            contextHolder!!.gpsTracker = GpsTracker(AndroidContextHolder.getContext()) {
                    temp ->
            }
        }

        fun getContext(): Context { return contextHolder!!.get() }

        fun getLocation(): Location? {
            return contextHolder!!.gpsTracker?.getLocation()
        }
    }
}
