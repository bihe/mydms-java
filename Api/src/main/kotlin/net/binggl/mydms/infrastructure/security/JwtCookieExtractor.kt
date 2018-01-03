package net.binggl.mydms.infrastructure.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class JwtCookieExtractor(@Value("\${auth.cookieName}") private val cookieName: String,
                         @Autowired private val request: HttpServletRequest) {

    fun extractToken(): String {
        val cookies = request.cookies

        val jwtCookie = cookies?.filter({ it.name == cookieName })?.first()
        if (jwtCookie == null) {
            LOG.warn("No cookies available!")
        }

        val cookieValue = jwtCookie?.value ?: ""
        if (cookieValue.isEmpty()) {
            LOG.warn("The authentication cookie is empty!")
        }
        return cookieValue
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JwtCookieExtractor::class.java)
    }
}