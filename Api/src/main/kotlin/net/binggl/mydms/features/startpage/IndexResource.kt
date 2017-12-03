package net.binggl.mydms.features.startpage

import net.binggl.mydms.features.startpage.models.AppInfo
import net.binggl.mydms.features.startpage.models.UserInfo
import net.binggl.mydms.features.startpage.models.VersionInfo
import net.binggl.mydms.infrastructure.security.ApiJwtSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexResource : BaseResource() {

    @ApiJwtSecured(url = "https://1mydms.binggl.net/", requiredRole = Role.User)
    @GetMapping("/api/v1/appinfo")
    fun applicationInfo() : AppInfo {
        val userInfo = UserInfo(this.user.userId, this.user.userName, this.user.displayName, this.user.claims)
	    val versionInfo = VersionInfo("artefactId", "buildNumber", "version")

        return AppInfo(userInfo, versionInfo)
    }
}