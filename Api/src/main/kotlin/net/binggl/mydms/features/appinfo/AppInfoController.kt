package net.binggl.mydms.features.appinfo

import net.binggl.mydms.features.appinfo.models.AppInfo
import net.binggl.mydms.features.appinfo.models.UserInfo
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.application.AppVersionInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppInfoController() : BaseResource() {

    @GetMapping("/api/v1/appinfo")
    fun applicationInfo() : AppInfo {

        val userInfo = UserInfo(this.user.userId, this.user.userName, this.user.displayName, this.user.claims)
        return AppInfo(userInfo, AppVersionInfo.versionInfo)
    }
}