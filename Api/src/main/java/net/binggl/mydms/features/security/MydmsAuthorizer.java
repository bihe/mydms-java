package net.binggl.mydms.features.security;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.dropwizard.auth.Authorizer;
import net.binggl.mydms.features.security.models.Claim;
import net.binggl.mydms.features.security.models.User;

@Singleton
public class MydmsAuthorizer implements Authorizer<User> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MydmsAuthorizer.class);
	
	private String appName;
	private String appUrl;
	
	public MydmsAuthorizer appName(String appName) {
		this.appName = appName;
		return this;
	}

	public MydmsAuthorizer appUrl(String appUrl) {
		this.appUrl = appUrl;
		return this;
	}

	@Override
	public boolean authorize(User user, String role) {
		
		LOGGER.debug("Check authorization of user {}", user);
		
		boolean result = wrapEx(() -> {
			
			// check claims of user
			Optional<Claim> claim = user.getClaims().stream().filter(c -> 
				this.appName.equals(c.getName()) &&
				this.appUrl.equals(c.getUrl()) &&
				role.toLowerCase().equals(c.getRole().toString().toLowerCase()))
					.findFirst();
			
			return claim.isPresent();
		});
		
		return result;
	}
}