package hackatum.munichpulse.mvp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform