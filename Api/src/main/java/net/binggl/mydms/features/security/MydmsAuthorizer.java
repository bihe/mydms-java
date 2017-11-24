package net.binggl.mydms.features.security;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import io.dropwizard.auth.Authorizer;
import net.binggl.commons.crypto.HashHelper;
import net.binggl.mydms.features.security.models.Claim;
import net.binggl.mydms.features.security.models.User;

@Singleton
public class MydmsAuthorizer implements Authorizer<User> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MydmsAuthorizer.class);
	
	private final Cache<String, Boolean> authorizationCache = CacheBuilder.newBuilder()
		       .maximumSize(10)
		       .expireAfterWrite(60, TimeUnit.MINUTES)
		       .build();
	
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
			
			return authorizationCache.get(getKey(user, role), () -> {
			    
				Optional<Claim> claim = user.getClaims().stream().filter(c -> 
				this.appName.equals(c.getName()) &&
				this.compareUrls(this.appUrl, c.getUrl()) &&
				role.toLowerCase().equals(c.getRole().toString().toLowerCase()))
					.findFirst();
			
				return claim.isPresent();
			});
		});
		
		return result;
	}
	
	private String getKey(User user, String role) {
		return HashHelper.getSHA("User:", new Integer(user.hashCode()).toString(), ";Role:", role);
	}
	
	private boolean compareUrls(String urlA, String urlB) {
		
		return wrapEx(() -> {
			URL a = new URL(urlA);
			URL b = new URL(urlB);
			
			if(a.getProtocol().equals(b.getProtocol())
					&& a.getHost().equals(b.getHost())
					&& a.getPort() == b.getPort()) {
				
				LOGGER.debug("Matching of url succeeded for protocol/host/port urlA: {}, urlB: {}", a, b);
				
				String pathA = a.getPath();
				if(StringUtils.isEmpty(pathA)) {
					return true;
				}
				String pathB = b.getPath();
				
				if(pathB.startsWith(pathA)) {
					LOGGER.debug("The urlB starts with the same path as the urlA. urlA: {}, urlB: {}", pathA, pathB);
					return true;
				}
			}
			return false;
		});
	}
	
	
}