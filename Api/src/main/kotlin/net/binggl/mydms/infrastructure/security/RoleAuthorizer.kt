package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.infrastructure.exceptions.InvalidAuthorizationException
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.models.User
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL

@Component
class RoleAuthorizer(@Value("\${auth.url}") private val applicationUrl: String,
                     @Value("\${auth.name}") private val applicationName: String) {

    fun authorize(user: User, requiredRole: Role): Boolean {
        val authorized = user.claims.find {
            this.compareUrls(it.url, applicationUrl) && it.name == applicationName && (it.role == requiredRole || requiredRole == Role.None)
        } ?: throw InvalidAuthorizationException("Required role for given url not available!")
        return authorized != null
    }

    private fun compareUrls(urlA: String, urlB: String): Boolean {

        val a = URL(urlA)
        val b = URL(urlB)

        if (a.protocol == b.protocol
                && a.host == b.host
                && a.port == b.port) {

            LOG.debug("Matching of url succeeded for protocol/host/port urlA: {}, urlB: {}", a, b)

            if (StringUtils.isEmpty(a.path)) {
                return true
            }
            if (b.path.startsWith(a.path)) {
                LOG.debug("The urlB starts with the same path as the urlA. urlA: {}, urlB: {}", a.path, b.path)
                return true
            }
        }
        return false
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RoleAuthorizer::class.java)
    }
}