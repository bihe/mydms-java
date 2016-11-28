package net.binggl.mydms.features.security;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.google.inject.Inject;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.security.models.User;

@Provider
public class SecurityDynamicFeature extends AuthDynamicFeature {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	SecurityDynamicFeature(JwtAuthenticator authenticator, MydmsAuthorizer authorizer, Environment environment, MydmsConfiguration configuration) {
		super(new CookieFilter
				.Builder<User>()
				.setConfiguration(configuration)
				.setAuthenticator(authenticator)
				.setAuthorizer(authorizer)
				.buildAuthFilter());

		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder(User.class));
	}
}