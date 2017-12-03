package net.binggl.mydms.features.startpage

import net.binggl.mydms.features.startpage.models.AppInfo
import net.binggl.mydms.features.startpage.models.UserInfo
import net.binggl.mydms.features.startpage.models.VersionInfo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagHandler() {

    @GetMapping("/api/v1/appinfo")
    fun applicationInfo() : AppInfo {
        val userInfo = UserInfo("ID", "NAME", "DISPLAYNAME", null)
	    val versionInfo = VersionInfo("artefactId", "buildNumber", "version")

        return AppInfo(userInfo, versionInfo)
    }
}