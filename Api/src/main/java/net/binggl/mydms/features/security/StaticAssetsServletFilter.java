package net.binggl.mydms.features.security;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import net.binggl.mydms.application.MydmsException;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.security.models.ErrorResult;
import net.binggl.mydms.features.security.models.User;
import net.binggl.mydms.features.shared.JsonUtils;

public class StaticAssetsServletFilter implements javax.servlet.Filter {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticAssetsServletFilter.class);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	private final JwtAuthenticator authenticator;
	private final MydmsAuthorizer authorizer;
	private final MydmsConfiguration configuration;
	private final String cookieName;
	
	public StaticAssetsServletFilter(JwtAuthenticator authenticator, MydmsAuthorizer authorizer, MydmsConfiguration configuration) {
		this.authenticator = authenticator;
		this.authorizer = authorizer;
		this.configuration = configuration;
		this.cookieName = configuration.getApplication().getSecurity().getCookieName();
		
		this.authenticator.tokenSecret(this.configuration.getApplication().getSecurity().getTokenSecret());
		this.authorizer
			.appName(this.configuration.getApplication().getSecurity().getAppName())
			.appUrl(this.configuration.getApplication().getSecurity().getAppUrl());
	}
	
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
        	try {
	            this.authenticate((HttpServletRequest)request);
	            chain.doFilter(request, response); 
        	} catch(MydmsException webEx) {
        		
        		String message = String.format("Could not authentication the request %s!", webEx.getMessage());
        		LOGGER.error(message);
        		HttpServletResponse httpResponse = (HttpServletResponse)response;
        		        		
        		if(webEx.isBrowserRequest()) {
        			
        			httpResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        			httpResponse.setHeader("Location", "/403");
        			return;
        			
        		} else { 		
        			ErrorResult result = new ErrorResult(HttpStatus.SC_UNAUTHORIZED, message);
        			
        			httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
        			httpResponse.setContentType("application/json");
        			httpResponse.getWriter().print(MAPPER.writeValueAsString(result));
        		}
        	}
        }
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
	public static String getName() {
		return "StaticAssetsServletFilter";
	}
	
	private void authenticate(HttpServletRequest request) {
		
		boolean treatAsBrowser = JsonUtils.isBrowserRequest(request);
		
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			LOGGER.warn("No cookies available. Cannot authenticate!");
			String message = "No authentication cookies available!";
			throw new MydmsException(message, HttpStatus.SC_UNAUTHORIZED).browserRequest(treatAsBrowser);
		}

		List<Cookie> cookieList = Arrays.asList(cookies);
		Optional<Cookie> jwtCookie = cookieList.stream().filter(a -> a.getName().equals(this.cookieName)).findFirst();

		if (!jwtCookie.isPresent()) {
			LOGGER.warn("No authentication cookie available!");
			String message = "No authentication cookie available!";
			throw new MydmsException(message, HttpStatus.SC_UNAUTHORIZED).browserRequest(treatAsBrowser);
		}

		if (StringUtils.isEmpty(jwtCookie.get().getValue())) {
			LOGGER.warn("The authentication cookie is empty!");
			String message = "The authentication cookie is empty!";
			throw new MydmsException(message, HttpStatus.SC_UNAUTHORIZED).browserRequest(treatAsBrowser);
		}
		
		Optional<User> user = wrapEx(() -> {
			return this.authenticator.authenticate(jwtCookie.get().getValue());
		});
		
		if(!user.isPresent()) {
			LOGGER.warn("Could not authenticate the user by the token!");
			String message = "Could not authenticate the user by the token!";
			throw new MydmsException(message, HttpStatus.SC_UNAUTHORIZED).browserRequest(treatAsBrowser);
		}
		
		if(!this.authorizer.authorize(user.get(), this.configuration.getApplication().getSecurity().getRequiredRole())) {
			String message = "Could not authorize the user!";
			throw new MydmsException(message, HttpStatus.SC_UNAUTHORIZED).browserRequest(treatAsBrowser);
		}	
	}
}