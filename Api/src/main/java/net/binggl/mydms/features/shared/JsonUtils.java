package net.binggl.mydms.features.shared;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

public class JsonUtils {
	
	private static final String AJAX_HEADER = "x-requested-with";
	private static final String AJAX_HEADER_STRING = "xmlhttprequest";
	private static final String AJAX_MEDIA_TYPE = "application/json";

	public static boolean isBrowserRequest(HttpServletRequest request) {
		
		boolean isAjaxRequest = false;
		boolean isAjaxMediaType = false;
		boolean treatAsBrowser = true;
				
		String contentType = request.getContentType();
		if(contentType != null && AJAX_MEDIA_TYPE.equals(contentType.toLowerCase())) {
			isAjaxMediaType = true;
		}
		String ajaxHeaderValue = request.getHeader(AJAX_HEADER);
		if(ajaxHeaderValue != null && AJAX_HEADER_STRING.equals(ajaxHeaderValue.toLowerCase())) {
			isAjaxRequest = true;
		}
		
		if(isAjaxMediaType || isAjaxRequest) {
			treatAsBrowser = false;
		}
		
		return treatAsBrowser;
	}
	
	public static boolean isBrowserRequest(ContainerRequestContext requestContext) {
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
		
		return treatAsBrowser;
	}
}
