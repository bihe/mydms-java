package net.binggl.mydms.shared.application

import net.binggl.mydms.features.appinfo.AppInfoController
import net.binggl.mydms.shared.models.VersionInfo
import java.util.*

object AppVersionInfo {

    private val properties = Properties()

    val versionInfo: VersionInfo
        get() {

            if(!properties.containsKey("artifactId")
                    || !properties.containsKey("build.number")
                    || !properties.containsKey("version")
                    ) {
                properties.load(AppInfoController::class.java.classLoader.getResourceAsStream("version.properties"))
            }

            return VersionInfo(artifactId = artifactId,
                    buildNumber = buildNumber,
                    version = version)
        }

    private val artifactId: String
        get() {
            return properties.getProperty("artifactId") ?: "-"
        }

    private val buildNumber: String
        get() {
            return properties.getProperty("build.number") ?: "-"
        }

    private val version: String
        get() {
            return properties.getProperty("version") ?: "-"
        }
}