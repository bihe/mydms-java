package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.infrastructure.error.InvalidAuthenticationException
import net.binggl.mydms.infrastructure.error.InvalidAuthorizationException
import net.binggl.mydms.shared.models.Role
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SimpleSwaggerSecurity(@Autowired private val jwtAuthenticator: JwtAuthenticator,
                            @Autowired private val roleAuthorizer: RoleAuthorizer,
                            @Autowired private val jwtHeaderExtractor: JwtHeaderExtractor,
                            @Autowired private val jwtCookieExtractor: JwtCookieExtractor,
                            @Value("\${application.security.swagger}") private val roles: List<Role> ): Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse,
                          chain: FilterChain) {

        if (req is HttpServletRequest && res is HttpServletResponse) {
            val requestPath = req.requestURI
            val matcher = AntPathMatcher()
            val isSwaggerPath = swaggerPaths.any { matcher.match(it, requestPath) }

            if(isSwaggerPath) {
                try {

                    var jwtToken: String
                    jwtToken = jwtHeaderExtractor.extractToken()
                    if (StringUtils.isEmpty(jwtToken)) {
                        LOG.debug("No JWT in Authorization header, fallback to cookies.")
                        jwtToken = jwtCookieExtractor.extractToken()
                    }
                    if (StringUtils.isEmpty(jwtToken)) {
                        throw InvalidAuthenticationException("No JWT available in Authorization header or cookie!")
                    }
                    val user = jwtAuthenticator.authenticate(jwtToken)
                    if (!user.isPresent) {
                        res.sendRedirect("/login")
                        return
                    }
                    for (role in roles) {
                        if (roleAuthorizer.authorize(user.get(), role)) {
                            // stop at first successfully authorized role
                            break
                        }
                    }
                } catch (authenticationEX: InvalidAuthenticationException) {
                    res.sendRedirect("/login")
                    return
                } catch (authorizationEX: InvalidAuthorizationException) {
                    res.sendRedirect("/login")
                    return
                }
            }
            chain.doFilter(req, res)
        }
    }

    override fun destroy() {}

    @Throws(ServletException::class)
    override fun init(arg0: FilterConfig) {
    }

    companion object {
        val swaggerPaths = listOf(
                "/**/swagger-ui.html*",
                "/**/swagger-resources*",
                "/**/swagger-resources/**",
                "/**/v2/api-docs")

        private val LOG = LoggerFactory.getLogger(SimpleSwaggerSecurity::class.java)
    }


}