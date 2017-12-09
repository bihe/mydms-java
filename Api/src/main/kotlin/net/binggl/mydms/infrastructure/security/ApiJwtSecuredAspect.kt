package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.infrastructure.exceptions.InvalidAuthenticationException
import net.binggl.mydms.infrastructure.exceptions.InvalidAuthorizationException
import net.binggl.mydms.shared.models.Role
import org.apache.commons.lang3.StringUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import java.net.URL
import javax.servlet.http.HttpServletRequest


// https://github.com/gothinkster/kotlin-spring-realworld-example-app/blob/master/src/main/kotlin/io/realworld/jwt/ApiKeySecuredAspect.kt

/**
 * Aspect whose goal is to check automatically that methods
 * having a @ApiKeySecured annotation are correctly accessed
 * by users having a valid API Key (JWT).
 * A check against the user service is done to find the user
 * having the api key passed as request header/parameter.
 * If the API Key is valid the annotated method is executed,
 * otherwise the response is set with an UNAUTHORIZED status and the annotated
 * method is not executed.
 */
@Aspect
@Component
class ApiJwtSecuredAspect(@Autowired private val userService: UserService,
                          @Autowired private val jwtAuthenticator: JwtAuthenticator,
                          @Autowired private val jwtCookieExtractor: JwtCookieExtractor,
                          @Autowired private val request: HttpServletRequest,
                          @Value("\${auth.url}") private val applicationUrl: String,
                          @Value("\${auth.name}") private val applicationName: String) {

    @Pointcut(value = "execution(@net.binggl.mydms.infrastructure.security.ApiSecured * *.*(..))")
    fun securedApiPointcut() {
    }

    @Around("securedApiPointcut()")
    @Throws(Throwable::class)
    fun aroundSecuredApiPointcut(joinPoint: ProceedingJoinPoint): Any? {
        if (request.method == "OPTIONS")
            return joinPoint.proceed()

        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val annotation = method.getAnnotation(ApiSecured::class.java)

        val user = jwtAuthenticator.authenticate(jwtCookieExtractor.extractToken())
        if(!user.isPresent) {
            throw InvalidAuthenticationException("Could not authenticate user!")
        }

        // verify the annotations
        val requiredRole = annotation.requiredRole

        user.get().claims.find {
            this.compareUrls(it.url, applicationUrl) && it.name == applicationName && (it.role == requiredRole || requiredRole == Role.None)
        } ?: throw InvalidAuthorizationException("Required role for given url not available!")

        // execute
        try {
            LOG.debug("Set user into request-holder: ${user.get().displayName}")
            userService.setCurrentUser(user.get())

            val result = joinPoint.proceed()

            userService.clearCurrentUser()
            LOG.debug("Cleanup user from request-holder.")
            return result
        } catch (e: Throwable) {
            userService.clearCurrentUser()

            val rs = e.javaClass.getAnnotation(ResponseStatus::class.java)
            if (rs != null) {
                LOG.error("ERROR accessing resource, reason: '{}', status: {}.",
                        if (StringUtils.isEmpty(e.message)) rs.reason else e.message,
                        rs.value)
            } else {
                LOG.error("ERROR accessing resource")
            }
            throw e
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

    companion object {
        private val LOG = LoggerFactory.getLogger(ApiJwtSecuredAspect::class.java)
    }
}