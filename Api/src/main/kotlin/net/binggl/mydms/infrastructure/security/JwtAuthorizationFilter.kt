package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.infrastructure.error.InvalidAuthenticationException
import net.binggl.mydms.infrastructure.error.InvalidAuthorizationException
import net.binggl.mydms.shared.models.User
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(authManager: AuthenticationManager,
                             private val roleAuthorizer: RoleAuthorizer,
                             private val jwtAuthenticator: JwtAuthenticator,
                             private val cookieName: String
                             ) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest,
                                  res: HttpServletResponse,
                                  chain: FilterChain) {
        val authentication = getAuthentication(req)
        SecurityContextHolder.getContext().authentication = authentication

        if (authentication == null) {
            throw InvalidAuthorizationException("Authorization needed!")
        }
        chain.doFilter(req, res)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        var jwtToken: String
        jwtToken = this.extractTokenFromAuthHeader(request)
        if (StringUtils.isEmpty(jwtToken)) {
            LOG.debug("No JWT in Authorization header, fallback to cookies.")
            jwtToken = this.extractTokenFromCookie(request)
        }
        if (StringUtils.isEmpty(jwtToken)) {
            throw InvalidAuthenticationException("No JWT token found!")
        }

        val user = jwtAuthenticator.authenticate(jwtToken)
        if(!user.isPresent) {
            return null
        }
        return UsernamePasswordAuthenticationToken(user.get(), null, this.getAuthorities(user.get()))
    }

    private fun getAuthorities(user: User): List<GrantedAuthority> {
        val roles = this.roleAuthorizer.getValidApplicationRoles(user)
        return roles.map {
            SimpleGrantedAuthority(it)
        }
    }

    private fun extractTokenFromAuthHeader(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization") ?: ""

        if (authHeader.indexOf("Bearer") > -1) {
            return authHeader.replace("Bearer ", "")
        }

        LOG.debug("No Bearer token in Authorization header available!")
        return ""
    }

    private fun extractTokenFromCookie(request: HttpServletRequest): String {
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
        private val LOG = LoggerFactory.getLogger(JwtAuthorizationFilter::class.java)
    }
}