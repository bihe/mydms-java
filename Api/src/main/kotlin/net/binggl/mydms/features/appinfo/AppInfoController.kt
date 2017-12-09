package net.binggl.mydms.features.appinfo

import net.binggl.mydms.features.appinfo.models.AppInfo
import net.binggl.mydms.features.appinfo.models.UserInfo
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.application.AppVersionInfo
import net.binggl.mydms.shared.models.Role
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppInfoController() : BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/appinfo")
    fun applicationInfo() : AppInfo {
        val userInfo = UserInfo(this.user.userId, this.user.userName, this.user.displayName, this.user.claims)

        return AppInfo(userInfo, AppVersionInfo.versionInfo)
    }
}