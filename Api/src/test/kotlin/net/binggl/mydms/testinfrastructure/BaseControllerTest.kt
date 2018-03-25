package net.binggl.mydms.testinfrastructure

import net.binggl.mydms.infrastructure.security.JwtAuthenticator
import net.binggl.mydms.infrastructure.security.RoleAuthorizer
import net.binggl.mydms.shared.util.MessageIntegrity
import org.springframework.boot.test.mock.mockito.MockBean

/**
 * all those services need to be available for an integration test
 * the subclass is responsible to add meaningfull contents to the beans
 */
abstract class BaseControllerTest {

    @MockBean
    private lateinit var msgInt: MessageIntegrity

    @MockBean
    private lateinit var jwtAuthenticator: JwtAuthenticator

    @MockBean
    private lateinit var roleAuthorizer: RoleAuthorizer
}