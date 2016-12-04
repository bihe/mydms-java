package net.binggl.mydms.features.security;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.auth0.jwt.JWTVerifier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import net.binggl.mydms.features.security.models.Claim;
import net.binggl.mydms.features.security.models.Role;
import net.binggl.mydms.features.security.models.User;
import net.binggl.mydms.features.security.models.User.UserBuilder;

@Singleton
public class JwtAuthenticator implements Authenticator<String, User> {

	private static final String USER_ID = "UserId";
	private static final String USERNAME = "UserName";
	private static final String DISPLAYNAME = "DisplayName";
	private static final String EMAIL = "Email";
	private static final String CLAIMS = "Claims";
	private static final String TYPE = "Type";
	private static final String TYPE_VALUE = "login.User";
	private static final List<String> KEYS = Arrays.asList(USER_ID, USERNAME, DISPLAYNAME, EMAIL, CLAIMS, TYPE);

	private final Cache<String, User> userCache = CacheBuilder.newBuilder()
		       .maximumSize(10)
		       .expireAfterWrite(60, TimeUnit.MINUTES)
		       .build();
	
	
	private String tokenSecret;

	public JwtAuthenticator tokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
		return this;
	}

	@Override
	public Optional<User> authenticate(String authentication) throws AuthenticationException {
		User user = wrapEx(() -> {
			return userCache.get(authentication, () -> {
			    return this.verifyToken(authentication);
			});
		});
		return Optional.ofNullable(user);
	}

	@SuppressWarnings("unchecked")
	private User verifyToken(String token) {
		User result = null;
		JWTVerifier jwtVerifier = new JWTVerifier(this.tokenSecret);

		result = wrapEx(() -> {
			Map<String, Object> payload = jwtVerifier.verify(token);
			if (payload != null) {
				if (payload.size() == KEYS.size() && payload.get(TYPE) != null
						&& TYPE_VALUE.equals(payload.get(TYPE))) {

					List<Claim> claims = this.parseClaims((List<String>) payload.get(CLAIMS));
					
					User jwtUser = new UserBuilder()
							.userId((String) payload.get(USER_ID))
							.userName((String) payload.get(USERNAME))
							.displayName((String) payload.get(DISPLAYNAME))
							.email((String) payload.get(EMAIL))
							.claims(claims)
							.build();
					
					return jwtUser;
				}
			}
			return null;
		});

		return result;
	}
	
	private List<Claim> parseClaims(List<String> claimList) {
		List<Claim> claims = new ArrayList<>();
		for(String entry : claimList) {
			String[] entries = entry.split(Pattern.quote("|"));
			if(entries != null && entries.length == 3) {
				Claim claim = new Claim(entries[0], entries[1], Role.fromString(entries[2]));
				claims.add(claim);
			}
		}
		return claims;
	}

}