package net.binggl.mydms.infrastructure.security

import com.auth0.jwt.exceptions.InvalidClaimException
import net.binggl.mydms.shared.models.Claim
import net.binggl.mydms.shared.models.Role
import org.junit.Assert
import org.junit.Test

class JwtAuthenticatorTest : JwtAuthenticator(tokenIssuer = "login.binggl.net", tokenSubject = "login.User", tokenSecret = "secret") {

    val payload = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWExZTRjY2QwOWE0Y2Y4YWE0YzEzM2U5YjM5NjkyNSIsImlhdCI6MTUxMzk2NTM0NiwiaXNzIjoibG9naW4uYmluZ2dsLm5ldCIsInN1YiI6ImxvZ2luLlVzZXIiLCJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsiYXxodHRwczovL2EuYi5kZS98dXNlciJdLCJVc2VySWQiOiIxMjM0IiwiRGlzcGxheU5hbWUiOiJIZW5yaWsgQmluZ2dsIn0._Fj1RRUnh_2HBfVmLPvtTD9CAOMoyQZS24v8Qlgll8s"

    @Test
    fun parseClaims() {
        val claims: List<Claim> = this.parseClaims(listOf("a|https://a.b.de/|user"))
        Assert.assertTrue(!claims.isEmpty())
        Assert.assertEquals(1, claims.size)
        Assert.assertEquals("a", claims[0].name)
        Assert.assertEquals("https://a.b.de/", claims[0].url)
        Assert.assertEquals(Role.User, claims[0].role)
    }

    @Test(expected = InvalidClaimException::class)
    fun verifyTokenFailIssuer() {
        val failIssuer = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWExZTRjY2QwOWE0Y2Y4YWE0YzEzM2U5YjM5NjkyNSIsImlhdCI6MTUxMzk2NTM0NiwiaXNzIjoiLSIsInN1YiI6ImxvZ2luLlVzZXIiLCJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsiYXxodHRwczovL2EuYi5kZS98dXNlciJdLCJVc2VySWQiOiIxMjM0IiwiRGlzcGxheU5hbWUiOiJIZW5yaWsgQmluZ2dsIn0.gKbacz6gN9BX18VfKhFYMGtPPhjyL7uiD1DNulMGLnE"
        this.verifyToken(failIssuer)
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