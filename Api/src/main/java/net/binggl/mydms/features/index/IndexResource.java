package net.binggl.mydms.features.index;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

import io.dropwizard.auth.Auth;
import net.binggl.mydms.application.Globals;
import net.binggl.mydms.application.Mydms403View;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.security.models.User;

@Path("/")
public class IndexResource implements Globals {

	private final MydmsConfiguration configuration;
	
	@Inject
	public IndexResource(MydmsConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@GET
	@RolesAllowed("User")
	public Response home() throws URISyntaxException {
		Response response = Response.temporaryRedirect(new URI(configuration.getApplication().getApplicationStartUrl())).build();
		return response;
	}
	
	@GET
	@Path("403")
	@Produces(MediaType.TEXT_HTML)
	public Mydms403View show403() {
		Mydms403View view = new Mydms403View();
		view.setLoginUrl(configuration.getApplication().getSecurity().getLoginUrl());
		return view;
	}
	
	@GET
	@Path("userinfo")
	@RolesAllowed("User")
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfo getUser(@Auth User user) {
		return new UserInfo(user);
	}
	
}