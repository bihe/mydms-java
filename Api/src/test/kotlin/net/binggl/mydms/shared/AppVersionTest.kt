package net.binggl.mydms.shared

import net.binggl.mydms.shared.application.AppVersionInfo
import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Test

class AppVersionTest {

    @Test
    fun testVersion() {
        val version = AppVersionInfo.versionInfo
        Assert.assertTrue(StringUtils.isNotEmpty(version.artifactId))
        Assert.assertTrue(StringUtils.isNotEmpty(version.buildNumber))
        Assert.assertTrue(StringUtils.isNotEmpty(version.version))
    }
}