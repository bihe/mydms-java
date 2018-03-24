package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.api.ApiUtils
import net.binggl.mydms.shared.util.MessageIntegrity
import net.binggl.mydms.shared.util.toBase64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SecurityAuthenticationEntryPoint(@Autowired private val msgIntegrity: MessageIntegrity) : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authenticationException: AuthenticationException?) {

        var isBrowserRequest = ApiUtils.isBrowserRequest(request)
        if (isBrowserRequest) {
            val message = msgIntegrity.generateValidMessage(authenticationException?.message ?: "")
            if (message.isPresent) {
                val msg = msgIntegrity.serialize(message = message.get())
                response.sendRedirect("/login/${msg.toBase64()}")
            } else {
                response.sendRedirect("/login")
            }
        } else {
            response.contentType = "application/json"
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.outputStream?.println("{ \"error\": \"" + authenticationException?.message + "\" }")
        }
    }
}