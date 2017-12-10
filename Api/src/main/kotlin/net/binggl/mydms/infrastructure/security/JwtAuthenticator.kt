package net.binggl.mydms.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.binggl.commons.crypto.HashHelper
import net.binggl.mydms.infrastructure.exceptions.InvalidAuthorizationException
import net.binggl.mydms.shared.models.Claim
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.models.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class JwtAuthenticator(@Value("\${auth.tokenSecret}") private val tokenSecret: String) {

    private val userCache : Cache<String, Optional<User>> = CacheBuilder.newBuilder()
		       .maximumSize(10)
		       .expireAfterWrite(60, TimeUnit.MINUTES)
		       .build()

    fun authenticate(token: String) : Optional<User> {
        return userCache.get(this.getKey(token), {
            this.verifyToken(token)
        })
    }

    protected fun verifyToken(token: String) : Optional<User> {
        val algorithm: Algorithm = Algorithm.HMAC256(this.tokenSecret)
        val verifier: JWTVerifier = JWT.require(algorithm)
                //.withIssuer("auth0") might be useful - needs to be implemented in login.binggl.net
                .build() //Reusable verifier instance

        val jwt: DecodedJWT
        try {
            jwt = verifier.verify(token)
        } catch (sigEx: SignatureVerificationException) {
            throw InvalidAuthorizationException("Could not verify the token checksum!")
        }

        val tokenClaims: Map<String, com.auth0.jwt.interfaces.Claim> = jwt.claims
        if (tokenClaims != null) {
            if (tokenClaims.size >= 6 && tokenClaims[TYPE] != null
                    && TYPE_VALUE == tokenClaims[TYPE]!!.asString()) {

                val claims: List<Claim> = this.parseClaims(tokenClaims[CLAIMS]!!.asList(String::class.java))
                return Optional.of(User(
                        userId = tokenClaims[USER_ID]!!.asString(),
                        userName = tokenClaims[USERNAME]!!.asString(),
                        displayName = tokenClaims[DISPLAYNAME]!!.asString(),
                        email = tokenClaims[EMAIL]!!.asString(),
                        claims = claims
                ))
            }
        }

        return Optional.empty()
    }

    protected fun parseClaims(claimList : List<String>) : List<Claim> {
        return claimList
                .map { it.split("|") }
                .filter { !it.isEmpty() && it.size == 3 }
                .map { Claim(it[0], it[1], Role.fromString(it[2])) }
	}

    private fun getKey(token: String): String {
        return HashHelper.getSHA(token)
    }

    companion object {
        private const val USER_ID = "UserId"
        private const val USERNAME = "UserName"
        private const val DISPLAYNAME = "DisplayName"
        private const val EMAIL = "Email"
        private const val CLAIMS = "Claims"
        private const val TYPE = "Type"
        private const val TYPE_VALUE = "login.User"
    }
}