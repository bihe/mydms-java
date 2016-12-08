package net.binggl.mydms.features.security;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.application.Globals;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.security.models.User;

@Provider
public class SecurityDynamicFeature extends AuthDynamicFeature implements Globals {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDynamicFeature.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	SecurityDynamicFeature(JwtAuthenticator authenticator, MydmsAuthorizer authorizer, Environment environment,
			MydmsConfiguration configuration) {
		super(new CookieFilter.Builder<User>()
				.setCookieName(configuration.getApplication().getSecurity().getCookieName())
				.setAuthenticator(authenticator
						.tokenSecret(configuration.getApplication().getSecurity().getTokenSecret()))
				.setAuthorizer(authorizer
						.appName(configuration.getApplication().getSecurity().getAppName())
						.appUrl(configuration.getApplication().getSecurity().getAppUrl()))
				.buildAuthFilter());

		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder(User.class));
		
		LOGGER.info("Add servlet filter '{}' to protect assets folder {}", StaticAssetsServletFilter.getName(), ASSETS_PATH + "*");
		
		environment.servlets().addFilter(StaticAssetsServletFilter.getName(), new StaticAssetsServletFilter(authenticator, authorizer, configuration))
			.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, ASSETS_PATH + "*");
		
	}
}