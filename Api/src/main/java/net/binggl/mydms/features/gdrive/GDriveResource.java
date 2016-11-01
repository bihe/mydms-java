package net.binggl.mydms.features.gdrive;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;
import com.google.inject.Inject;

import net.binggl.mydms.features.shared.HashHelper;

@Path("gdrive")
public class GDriveResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(GDriveResource.class);
	private static final String SESSION_CORRELATION_TOKEN = "correlationToken";
	private GDriveClient client;
	
	@Inject
	public GDriveResource(GDriveClient client) {
		this.client = client;
	}

	@GET
	@Path("link")
	public Response link(@Context HttpServletRequest request) {
		Response response = Response.noContent().status(Status.BAD_GATEWAY).build();
		try {
			String correlationToken = HashHelper.getSHA("<USERID>", new Date().toString());
			
			HttpSession session = request.getSession(true);
			session.setAttribute(SESSION_CORRELATION_TOKEN, correlationToken);
			
			String redirectUrl = this.client.getRedirectUrl(correlationToken);
			response = Response.temporaryRedirect(new URI(redirectUrl)).build();
		} catch (Exception e) {
			LOGGER.error("Could not geta redirect URL {}", e.getMessage(), e);
		}
		return response;
	}

	@GET
	@Path("oauth2callback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response callback(@Context HttpServletRequest request, @QueryParam("code") String authorizationCode,
			@QueryParam("state") String correlationToken) {
		Response response = Response.noContent().status(Status.BAD_GATEWAY).build();
		try {
			
			HttpSession session = request.getSession(false);
			String token = (String) session.getAttribute(SESSION_CORRELATION_TOKEN);
			if(StringUtils.isNotEmpty(token) && token.equals(correlationToken)) {
			
				session.removeAttribute(SESSION_CORRELATION_TOKEN);
				
				Credential credentials = this.client.getCredentials(authorizationCode);
				LOGGER.debug("Got credentials: {}", credentials);
				
				List<File> files = this.client.getFiles(credentials);
	
				response = Response.ok().entity(files).build();
			} else {
				throw new WebApplicationException("The correlation token does not match!", Response.Status.FORBIDDEN);
			}

		} catch (Exception e) {
			LOGGER.error("Could not geta redirect URL {}", e.getMessage(), e);
		}
		return response;
	}

}
