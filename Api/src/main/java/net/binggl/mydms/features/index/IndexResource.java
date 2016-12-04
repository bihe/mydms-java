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

import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.shared.Globals;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("User")
public class IndexResource implements Globals {

	private final MydmsConfiguration configuration;
	
	@Inject
	public IndexResource(MydmsConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@GET
	public Response home() throws URISyntaxException {
		Response response = Response.temporaryRedirect(new URI(configuration.getApplication().getApplicationStartUrl())).build();
		return response;
	}
}