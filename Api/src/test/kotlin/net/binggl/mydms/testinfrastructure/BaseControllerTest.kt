package net.binggl.mydms.testinfrastructure

import net.binggl.mydms.infrastructure.security.*
import net.binggl.mydms.shared.util.MessageIntegrity
import org.springframework.boot.test.mock.mockito.MockBean

/**
 * all those services need to be available for an integration test
 * the subclass is responsible to add meaningfull contents to the beans
 */
abstract class BaseControllerTest {

    @MockBean
    private lateinit var usrSvc: UserService

    @MockBean
    private lateinit var msgInt: MessageIntegrity

    @MockBean
    private lateinit var jwtAuthenticator: JwtAuthenticator

    @MockBean
    private lateinit var roleAuthorizer: RoleAuthorizer

    @MockBean
    private lateinit var jwtCookieExtractor: JwtCookieExtractor

    @MockBean
    private lateinit var jwtHeaderExtractor: JwtHeaderExtractor
}