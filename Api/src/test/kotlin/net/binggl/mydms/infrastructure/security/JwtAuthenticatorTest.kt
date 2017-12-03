package net.binggl.mydms.infrastructure.security

import net.binggl.mydms.shared.models.Claim
import net.binggl.mydms.shared.models.Role
import org.junit.Assert
import org.junit.Test

class JwtAuthenticatorTest : JwtAuthenticator("secret") {

    val payload = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsiYXxodHRwczovL2EuYi5kZS98dXNlciJdLCJVc2VySWQiOiIxMjM0IiwiRGlzcGxheU5hbWUiOiJIZW5yaWsgQmluZ2dsIn0.p2O9xFt_FZJoJrEtV2nu4vYrqIAcbxV2P-DnuhYf9Ig"

    @Test
    fun parseClaims() {
        val claims: List<Claim> = this.parseClaims(listOf("a|https://a.b.de/|user"))
        Assert.assertTrue(!claims.isEmpty())
        Assert.assertEquals(1, claims.size)
        Assert.assertEquals("a", claims[0].name)
        Assert.assertEquals("https://a.b.de/", claims[0].url)
        Assert.assertEquals(Role.User, claims[0].role)
    }

    @Test
    fun verifyToken() {
        val user = this.verifyToken(payload)
        Assert.assertTrue(user.isPresent)
        Assert.assertEquals("Henrik Binggl", user.get().displayName)
        Assert.assertEquals("bihe", user.get().userName)
        Assert.assertEquals("a.b@c.de", user.get().email)
        Assert.assertEquals("https://a.b.de/", user.get().claims[0].url)
    }

    @Test
    fun authenticate() {
        var user = this.authenticate(payload)
        Assert.assertTrue(user.isPresent)
        Assert.assertEquals("Henrik Binggl", user.get().displayName)
        Assert.assertEquals("bihe", user.get().userName)
        Assert.assertEquals("a.b@c.de", user.get().email)
        Assert.assertEquals("https://a.b.de/", user.get().claims[0].url)

        // call the method again to see if the cache works
        user = this.authenticate(payload)
        Assert.assertTrue(user.isPresent)
        Assert.assertEquals("Henrik Binggl", user.get().displayName)
        Assert.assertEquals("bihe", user.get().userName)
        Assert.assertEquals("a.b@c.de", user.get().email)
        Assert.assertEquals("https://a.b.de/", user.get().claims[0].url)
    }
}