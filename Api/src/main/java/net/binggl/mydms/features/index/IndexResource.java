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

import net.binggl.mydms.application.Globals;
import net.binggl.mydms.application.Mydms403View;
import net.binggl.mydms.config.MydmsConfiguration;

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
		return new Mydms403View();
	}
	
}