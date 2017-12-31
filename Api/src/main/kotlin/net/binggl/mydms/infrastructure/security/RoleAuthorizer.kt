package net.binggl.mydms.infrastructure.security

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.util.concurrent.UncheckedExecutionException
import net.binggl.commons.crypto.HashHelper
import net.binggl.mydms.infrastructure.exceptions.InvalidAuthorizationException
import net.binggl.mydms.shared.models.Claim
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.models.User
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL
import java.util.concurrent.TimeUnit

@Component
class RoleAuthorizer(@Value("\${auth.url}") private val applicationUrl: String,
                     @Value("\${auth.name}") private val applicationName: String) {

    private val authorizationCache : Cache<String, Boolean> = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build()

    /**
     * authorize a given user
     * @param user the user object with authorization claims
     * @param requiredRole the minimum role necessary for the authorization process
     * @return success/failure (Boolean)
     * @throws InvalidAuthorizationException if the authorization cannot be done
     */
    fun authorize(user: User, requiredRole: Role): Boolean {
        try {
            return authorizationCache.get(this.getKey(user, requiredRole), auth@ {
                val authorized: Claim? = user.claims.find {

                    this.compareUrls(it.url, applicationUrl) &&
                            it.name == applicationName &&
                            (it.role.precedence >= requiredRole.precedence)

                } ?: throw InvalidAuthorizationException("Required role for given url not available!")
                return@auth authorized != null
            })
        } catch (ex: UncheckedExecutionException) {
            throw ex.cause ?: ex
        }
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

    private fun getKey(user: User, requiredRole: Role): String {
        return HashHelper.getSHA("${user.userId}__${user.userName}__$requiredRole")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RoleAuthorizer::class.java)
    }
}