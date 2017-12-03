package net.binggl.mydms.infrastructure.security

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
//@Aspect
//@Component
//class ApiJwtSecuredAspect(@Autowired val userService: UserService) {
//
//    @Autowired
//    var request: HttpServletRequest? = null
//
//    @Pointcut(value = "execution(@net.binggl.mydms.infrastructure.security.ApiJwtSecured * *.*(..))")
//    fun securedApiPointcut() {
//    }
//
//    @Around("securedApiPointcut()")
//    @Throws(Throwable::class)
//    fun aroundSecuredApiPointcut(joinPoint: ProceedingJoinPoint): Any? {
//        if (request!!.method == "OPTIONS")
//            return joinPoint.proceed()
//
//        // check for needed roles
//        val signature = joinPoint.signature as MethodSignature
//        val method = signature.method
//        val annotation = method.getAnnotation(ApiJwtSecured::class.java)
//
//        val apiKey = request!!.getHeader("Authorization")?.replace("Token ", "")
//
//        if (StringUtils.isEmpty(apiKey) && annotation.mandatory) {
//            LOG.info("No Authorization part of the request header/parameters, returning {}.", HttpServletResponse.SC_UNAUTHORIZED)
//
//            issueError(response)
//            return null
//        }
//
//        // find the user associated to the given api key.
//        var user = userService.findByToken(apiKey ?: "")
//        LOG.info("user by token: ${user?.email}")
//        if (user == null && anno.mandatory) {
//            LOG.info("No user with Authorization: {}, returning {}.", apiKey, HttpServletResponse.SC_UNAUTHORIZED)
//
//            issueError(response)
//            return null
//        } else {
//            // validate JWT
//            try {
//                LOG.info("Validating JWT")
//                if (!userService.validToken(apiKey ?: "", user ?: User())) {
//                    LOG.info("JWT invalid")
//                    if (!anno.mandatory && user == null) {
//                        LOG.info("No problem because not mandatory")
//                        user = User()
//                    } else { // error
//                        LOG.info("Authorization: {} is an invalid JWT.", apiKey, HttpServletResponse.SC_UNAUTHORIZED)
//
//                        issueError(response)
//                        return null
//                    }
//                }
//            } catch (e: Exception) {
//                if (anno.mandatory) {
//                    issueError(response)
//                    return null
//                } else
//                    user = User()
//            }
//        }
//
//        LOG.info("User is: ${user?.email}")
//        userService.setCurrentUser(user ?: User())
//
//        LOG.info("OK accessing resource, proceeding.")
//
//        // execute
//        try {
//            val result = joinPoint.proceed()
//
//            // remove user from thread local
//            userService.clearCurrentUser()
//
//            LOG.info("DONE accessing resource.")
//
//            return result
//        } catch (e: Throwable) {
//            // check for custom exception
//            val rs = e.javaClass.getAnnotation(ResponseStatus::class.java)
//            if (rs != null) {
//                LOG.error("ERROR accessing resource, reason: '{}', status: {}.",
//                        if (StringUtils.isEmpty(e.message)) rs.reason else e.message,
//                        rs.value)
//            } else {
//                LOG.error("ERROR accessing resource")
//            }
//            throw e
//        }
//
//    }
//
//
//    companion object {
//        private val LOG = LoggerFactory.getLogger(ApiJwtSecuredAspect::class.java)
//    }
//}