package net.binggl.mydms.test.security;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import io.dropwizard.auth.AuthenticationException;
import net.binggl.mydms.features.security.JwtAuthenticator;
import net.binggl.mydms.features.security.models.User;

public class SecurityTest {

	@Test
	public void testJwtVerify() throws AuthenticationException {
		JwtAuthenticator auth = new JwtAuthenticator();
		auth.tokenSecret("secret");
		
		String payload = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoibG9naW4uVXNlciIsIlVzZXJOYW1lIjoiYmloZSIsIkVtYWlsIjoiYS5iQGMuZGUiLCJDbGFpbXMiOlsiYXxodHRwczovL2EuYi5kZS98dXNlciJdLCJVc2VySWQiOiIxMjM0IiwiRGlzcGxheU5hbWUiOiJIZW5yaWsgQmluZ2dsIn0.p2O9xFt_FZJoJrEtV2nu4vYrqIAcbxV2P-DnuhYf9Ig";
		
		Optional<User> result = auth.authenticate(payload);
		assertTrue(result.isPresent());
		assertTrue("Henrik Binggl".equals(result.get().getDisplayName()));
		assertTrue("bihe".equals(result.get().getUserName()));
		assertTrue("a.b@c.de".equals(result.get().getEmail()));
		assertTrue("https://a.b.de/".equals(result.get().getClaims().get(0).getUrl()));
	}
}
