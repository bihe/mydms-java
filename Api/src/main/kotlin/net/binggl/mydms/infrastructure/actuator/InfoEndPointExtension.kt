package net.binggl.mydms.infrastructure.actuator

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class InfoEndPointExtension(@Autowired private val ctx:ApplicationContext): InfoContributor {

    override fun contribute(builder: Info.Builder?) {
        val details = HashMap<String, Any>()
        val instant = Instant.ofEpochMilli(ctx.startupDate)
        val startupDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        details["application-name"] = ctx.applicationName
        details["startup-date"] = startupDate.toString()

        val inetAddr = InetAddress.getLocalHost()

        details["local-ip"] = inetAddr.hostAddress
        details["hostname"] = inetAddr.hostName

        builder?.withDetail("context", details)
    }
}
