rootProject.name = "MunichPulse"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                val token = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").getOrNull()
                    ?: System.getenv("MAPBOX_DOWNLOADS_TOKEN")
                    ?: run {
                        val props = java.util.Properties()
                        val localPropsFile = rootDir.resolve("local.properties")
                        if (localPropsFile.exists()) {
                            props.load(java.io.FileInputStream(localPropsFile))
                        }
                        props.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                    }
                if (token != null) {
                    println("Mapbox token found: ${token.take(4)}...${token.takeLast(4)}")
                } else {
                    println("Mapbox token NOT found")
                }
                username = "mapbox"
                password = token
            }

            
        }
    }
}

include(":composeApp")
