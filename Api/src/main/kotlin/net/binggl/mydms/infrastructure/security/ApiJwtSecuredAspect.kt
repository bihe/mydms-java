package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.infrastructure.error.InvalidAuthenticationException
import org.apache.commons.lang3.StringUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
                          @Autowired private val roleAuthorizer: RoleAuthorizer,
                          @Autowired private val jwtCookieExtractor: JwtCookieExtractor,
                          @Autowired private val jwtHeaderExtractor: JwtHeaderExtractor,
                          @Autowired private val request: HttpServletRequest) {

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
        if(!user.isPresent) {
            throw InvalidAuthenticationException("Could not authenticate user!")
        }

        roleAuthorizer.authorize(user.get(), annotation.requiredRole)

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
            throw e
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApiJwtSecuredAspect::class.java)
    }
}