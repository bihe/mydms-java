package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.models.Role
import org.junit.Assert
import org.junit.Test

class RoleTests {

    @Test
    fun testRolePrecedence() {
        val none = Role.None
        val user = Role.User
        val admin = Role.Admin

        Assert.assertTrue(none.precedence < user.precedence)
        Assert.assertTrue(user.precedence < admin.precedence)
        Assert.assertTrue(none.precedence < admin.precedence)
        Assert.assertFalse(user.precedence > admin.precedence)
        Assert.assertFalse(none.precedence > admin.precedence)
        Assert.assertFalse(none.precedence > user.precedence)
    }

    @Test
    fun parseRoles() {
        Assert.assertEquals(Role.None, Role.valueOf("None"))
        Assert.assertEquals(Role.User, Role.valueOf("User"))
        Assert.assertEquals(Role.Admin, Role.valueOf("Admin"))
        Assert.assertEquals(Role.Admin, Role.fromString("admin"))

        Assert.assertNotEquals(Role.Admin, Role.valueOf("User"))
    }
}