package net.binggl.mydms.features.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.AuthFilter;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.config.SecurityConfiguration;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class CookieFilter<P extends Principal> extends AuthFilter<String, P> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CookieFilter.class);
	private final SecurityConfiguration configuration;
	private final String CookieName;

	private CookieFilter(MydmsConfiguration config) {
		this.configuration = config != null ? config.getApplication().getSecurity() : null;
		this.CookieName = this.configuration != null ? this.configuration.getCookieName() : "login_token";
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		Map<String, Cookie> cookies = requestContext.getCookies();
		if (cookies == null) {
			LOGGER.warn("No cookies available. Cannot authenticate!");
			String message = "No authentication cookies available!";
			throw new WebApplicationException(message, Response.Status.UNAUTHORIZED);
		}

		Optional<String> jwtCookie = cookies.keySet().stream().filter(a -> a.equals(CookieName)).findFirst();

		if (!jwtCookie.isPresent()) {
			LOGGER.warn("No authentication cookie available!");
			String message = "No authentication cookie available!";
			throw new WebApplicationException(message, Response.Status.UNAUTHORIZED);
		}

		Cookie cookiePayload = cookies.get(CookieName);

		if (cookiePayload == null || StringUtils.isEmpty(cookiePayload.getValue())) {
			LOGGER.warn("The authentication cookie is empty!");
			String message = "The authentication cookie is empty!";
			throw new WebApplicationException(message, Response.Status.UNAUTHORIZED);
		}

		if (!authenticate(requestContext, cookiePayload.getValue(), "JWT")) {
			LOGGER.warn("Could not authenticate cookie payload {}!", cookiePayload.getValue());
			String message = "Could not authenticate!";
			throw new WebApplicationException(message, Response.Status.UNAUTHORIZED);
		}
	}

	public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, CookieFilter<P>> {

		private MydmsConfiguration configuration;

		public Builder<P> setConfiguration(MydmsConfiguration configuration) {
			this.configuration = configuration;
			return this;
		}

		@Override
		protected CookieFilter<P> newInstance() {
			return new CookieFilter<>(this.configuration);
		}
	}
}