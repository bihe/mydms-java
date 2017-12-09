package net.binggl.mydms.features.startpage

import net.binggl.mydms.features.startpage.models.AppInfo
import net.binggl.mydms.features.startpage.models.UserInfo
import net.binggl.mydms.features.startpage.models.VersionInfo
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class IndexResource(private var properties: Properties) : BaseResource() {

    init {
        properties = Properties()
        properties.load(IndexResource::class.java.classLoader.getResourceAsStream("version.properties"))
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/appinfo")
    fun applicationInfo() : AppInfo {
        val userInfo = UserInfo(this.user.userId, this.user.userName, this.user.displayName, this.user.claims)
	    val versionInfo = VersionInfo(artifactId = this.artifactId,
                buildNumber = this.buildNumber,
                version = this.version)

        return AppInfo(userInfo, versionInfo)
    }

    private val artifactId: String
        get() {
            return this.properties.getProperty("artifactId") ?: "-"
        }

    private val buildNumber: String
        get() {
            return this.properties.getProperty("build.number") ?: "-"
        }

    private val version: String
        get() {
            return this.properties.getProperty("version") ?: "-"
        }
}