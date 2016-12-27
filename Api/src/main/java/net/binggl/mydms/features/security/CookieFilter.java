package net.binggl.mydms.features.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.AuthFilter;
import net.binggl.mydms.application.MydmsException;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class CookieFilter<P extends Principal> extends AuthFilter<String, P> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CookieFilter.class);
	private final String cookieName;
	private static final String AJAX_HEADER = "x-requested-with";
	private static final String AJAX_HEADER_STRING = "xmlhttprequest";
	private static final String AJAX_MEDIA_TYPE = "application/json";
	
	private CookieFilter(String cookieName) {
		this.cookieName = cookieName;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		boolean isAjaxRequest = false;
		boolean isAjaxMediaType = false;
		boolean treatAsBrowser = true;
		
		MediaType mediaType = requestContext.getAcceptableMediaTypes().get(0);
		if(mediaType != null && AJAX_MEDIA_TYPE.equals(mediaType.toString().toLowerCase())) {
			isAjaxMediaType = true;
		}
		String ajaxHeaderValue = requestContext.getHeaderString(AJAX_HEADER);
		if(ajaxHeaderValue != null && AJAX_HEADER_STRING.equals(ajaxHeaderValue.toLowerCase())) {
			isAjaxRequest = true;
		}
		
		if(isAjaxMediaType || isAjaxRequest) {
			treatAsBrowser = false;
		}
		
		
		Map<String, Cookie> cookies = requestContext.getCookies();
		if (cookies == null) {
			LOGGER.warn("No cookies available. Cannot authenticate!");
			String message = "No authentication cookies available!";
			throw new MydmsException(message, Response.Status.UNAUTHORIZED).browserRequest(treatAsBrowser);
		}

		Optional<String> jwtCookie = cookies.keySet().stream().filter(a -> a.equals(cookieName)).findFirst();

		if (!jwtCookie.isPresent()) {
			LOGGER.warn("No authentication cookie available!");
			String message = "No authentication cookie available!";
			throw new MydmsException(message, Response.Status.UNAUTHORIZED).browserRequest(treatAsBrowser);
		}

		Cookie cookiePayload = cookies.get(cookieName);

		if (cookiePayload == null || StringUtils.isEmpty(cookiePayload.getValue())) {
			LOGGER.warn("The authentication cookie is empty!");
			String message = "The authentication cookie is empty!";
			throw new MydmsException(message, Response.Status.UNAUTHORIZED).browserRequest(treatAsBrowser);
		}

		if (!authenticate(requestContext, cookiePayload.getValue(), "JWT")) {
			LOGGER.warn("Could not authenticate cookie payload {}!", cookiePayload.getValue());
			String message = "Could not authenticate!";
			throw new MydmsException(message, Response.Status.UNAUTHORIZED).browserRequest(treatAsBrowser);
		}
	}

	public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, CookieFilter<P>> {

		private String cookieName;
		
		public Builder<P> setCookieName(String cookieName) {
			this.cookieName = cookieName;
			return this;
		}
		
		@Override
		protected CookieFilter<P> newInstance() {
			return new CookieFilter<>(this.cookieName);
		}
	}
}