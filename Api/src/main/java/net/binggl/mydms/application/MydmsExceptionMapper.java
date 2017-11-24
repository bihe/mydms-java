package net.binggl.mydms.application;

import static net.binggl.commons.util.ExceptionHelper.wrapEx;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.dropwizard.jersey.errors.ErrorMessage;

@Provider
public class MydmsExceptionMapper implements ExceptionMapper<MydmsException>{

	@Override
	public Response toResponse(MydmsException exception) {
		
		if(!exception.isBrowserRequest()) {
			return Response
					.status(exception.getResponse().getStatus())
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(new ErrorMessage(exception.getResponse().getStatus(),
							exception.getMessage()))
					.build();
		} 
		
		return wrapEx(() -> {
			return Response.temporaryRedirect(new URI("/403")).build();
		});
	}
}
