package net.binggl.mydms.infrastructure.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class JwtHeaderExtractor(@Autowired private val request: HttpServletRequest) {

    fun extractToken(): String {
        val authHeader = request.getHeader("Authorization") ?: ""

        if (authHeader.indexOf("Bearer") > -1) {
            return authHeader.replace("Bearer ", "")
        }

        LOG.debug("No Bearer token in Authorization header available!")
        return ""
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JwtHeaderExtractor::class.java)
    }
}