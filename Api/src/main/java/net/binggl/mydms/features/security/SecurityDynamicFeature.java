package net.binggl.mydms.features.security;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.google.inject.Inject;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.caching.Cache;
import net.binggl.mydms.features.security.models.User;

@Provider
public class SecurityDynamicFeature extends AuthDynamicFeature {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	SecurityDynamicFeature(JwtAuthenticator authenticator, MydmsAuthorizer authorizer, Environment environment,
			MydmsConfiguration configuration, Cache cache) {
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
	}
}