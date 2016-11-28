package net.binggl.mydms.features.security;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.dropwizard.auth.Authorizer;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.config.SecurityConfiguration;
import net.binggl.mydms.features.security.models.Claim;
import net.binggl.mydms.features.security.models.User;

@Singleton
public class MydmsAuthorizer implements Authorizer<User> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MydmsAuthorizer.class);
	
	private SecurityConfiguration configuration;
	
	@Inject
	public MydmsAuthorizer(MydmsConfiguration config) {
		this.configuration = config != null ? config.getApplication().getSecurity() : null;
	}
	
	@Override
	public boolean authorize(User user, String role) {
		
		LOGGER.debug("Check authorization of user {}", user);
		
		boolean result = wrapEx(() -> {
			
			// check claims of user
			Optional<Claim> claim = user.getClaims().stream().filter(c -> 
				this.configuration.getAppName().equals(c.getName()) &&
				this.configuration.getAppUrl().equals(c.getUrl())).findFirst();
			
			return claim.isPresent();
		});
		
		return result;
	}
}