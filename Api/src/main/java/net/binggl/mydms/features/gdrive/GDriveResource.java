package net.binggl.mydms.features.gdrive;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.binggl.commons.crypto.HashHelper;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.gdrive.client.GDriveClient;
import net.binggl.mydms.features.gdrive.models.GDriveCredential;
import net.binggl.mydms.features.gdrive.models.GDriveFile;
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore;
import net.binggl.mydms.features.shared.Globals;
import net.binggl.mydms.features.shared.models.ActionResult;
import net.binggl.mydms.features.shared.models.SimpleResult;

@Path("/api/gdrive")
@RolesAllowed("User")
public class GDriveResource implements Globals {

	private static final Logger LOGGER = LoggerFactory.getLogger(GDriveResource.class);
	private static final String SESSION_CORRELATION_TOKEN = "correlationToken";

	private GDriveClient client;
	private GDriveCredentialStore store;
	private MydmsConfiguration configuration;

	@Inject
	public GDriveResource(GDriveClient client, GDriveCredentialStore store, MydmsConfiguration configuration) {
		this.client = client;
		this.store = store;
		this.configuration = configuration;
	}

	@GET
	@Path("link")
	public Response link(@Context HttpServletRequest request) {
		Response response = Response.noContent().status(Status.BAD_GATEWAY).build();
		try {
			String correlationToken = HashHelper.getSHA(USER_TOKEN, new Date().toString());

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
	public Response callback(@Context HttpServletRequest request, @QueryParam("code") String authorizationCode,
			@QueryParam("state") String correlationToken) {
		Response response = Response.noContent().status(Status.BAD_GATEWAY).build();
		try {

			HttpSession session = request.getSession(false);
			String token = (String) session.getAttribute(SESSION_CORRELATION_TOKEN);
			if (StringUtils.isNotEmpty(token) && token.equals(correlationToken)) {

				session.removeAttribute(SESSION_CORRELATION_TOKEN);

				GDriveCredential credentials = this.client.getCredentials(authorizationCode, USER_TOKEN);
				LOGGER.debug("Got credentials: {}", credentials);
				this.store.save(USER_TOKEN, credentials);

				response = Response
						.temporaryRedirect(new URI(configuration.getApplication().getGoogle().getSuccessUrl())).build();
			} else {
				throw new WebApplicationException("The correlation token does not match!", Response.Status.FORBIDDEN);
			}

		} catch (Exception e) {
			LOGGER.error("Could not get a redirect URL {}", e.getMessage(), e);
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public SimpleResult unlinkAccount() {
		SimpleResult result = new SimpleResult("", ActionResult.None);
		try {
			this.store.clearCredentials(USER_TOKEN);
			result = new SimpleResult("Stored credentials where deleted.", ActionResult.Deleted);
		} catch (Exception e) {
			LOGGER.error("Could not unlink account {}", e.getMessage(), e);
			throw new WebApplicationException(String.format("Could not unlink account: %s!", e.getMessage()),
					Response.Status.BAD_GATEWAY);
		}
		return result;
	}

	@GET
	@Path("file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@QueryParam("path") String path) {
		Response result = Response.noContent().status(Status.NOT_FOUND).build();
		try {

			if (!this.store.isCredentialAvailable(USER_TOKEN))
				throw new WebApplicationException(String.format("Could not get files: Account is not linked!"),
						Response.Status.UNAUTHORIZED);

			LOGGER.debug("Supplied path {}", path);

			// the path is supplied in BASE64 Encoding
			byte[] decodedPathPayload = Base64.decodeBase64(path);
			String decodedPath = new String(decodedPathPayload, StandardCharsets.UTF_8);

			LOGGER.debug("Decoded path {}", decodedPath);

			Optional<GDriveFile> filePayload = this.client.getFile(cred(), decodedPath,
					configuration.getApplication().getGoogle().getParentDrivePath());
			if (filePayload.isPresent()) {
				result = Response.ok(filePayload.get().getPayload(), filePayload.get().getMimeType())
						.header("content-disposition", "attachment; filename = " + filePayload.get().getName()).build();
			}

		} catch (GDriveRuntimeException EX) {
			LOGGER.error("Could not get files {}", EX.getMessage(), EX);
			throw new WebApplicationException(String.format("Could not get file: %s!", EX.getMessage()),
					Response.Status.BAD_GATEWAY);
		}
		return result;
	}

	// @GET
	// @Path("isfolder")
	// @Produces(MediaType.TEXT_PLAIN)
	// public Response isFolderAvailable(@QueryParam("name") String folderName)
	// {
	// Response result = Response.noContent().status(Status.NOT_FOUND).build();
	//
	// try {
	//
	// FileItem item = new FileItem("abc.pdf", "application/pdf",
	// FileUtils.readFileToByteArray(new
	// File("/home/henrik/Downloads/kirchenbeitrag.pdf")), folderName);
	//
	// if(!this.fileService.saveFile(item)) {
	// throw new WebApplicationException(String.format("Could not upload file:
	// %s!", item.getFileName()),
	// Response.Status.BAD_GATEWAY);
	// }
	//
	// LOGGER.debug("Uploaded file {}", item);
	//
	// result = Response.ok("file uploaded: " + item.getFileName()).build();
	//
	// // if(this.store.isCredentialAvailable(USER_TOKEN)) {
	// // Optional<GDriveItem> folder = this.client.getFolder(cred(),
	// // folderName,
	// // configuration.getApplication().getGoogle().getParentDrivePath());
	// //
	// // if(!folder.isPresent()) {
	// // GDriveItem item = this.client.createFolder(cred(), folderName,
	// // configuration.getApplication().getGoogle().getParentDrivePath());
	// // LOGGER.debug("Created item {}", item);
	// // } else {
	// // GDriveItem item = this.client.saveItem(cred(), "abc.pdf",
	// // "application/pdf",
	// // FileUtils.readFileToByteArray(new
	// // File("/home/henrik/Downloads/kirchenbeitrag.pdf")),
	// // folder.get().getId());
	// // LOGGER.debug("Uploaded file {}", item);
	// //
	// // }
	// // } else {
	// // throw new WebApplicationException(String.format("Could not get
	// // files: Account is not linked!"), Response.Status.UNAUTHORIZED);
	// // }
	//
	// } catch (Exception EX) {
	// LOGGER.error("Could not check folder {}", EX.getMessage(), EX);
	// throw new WebApplicationException(String.format("Could not check folder:
	// %s!", EX.getMessage()),
	// Response.Status.BAD_GATEWAY);
	// }
	//
	// return result;
	// }

	private GDriveCredential cred() {
		GDriveCredential credentials = this.store.load(USER_TOKEN);
		LOGGER.debug("Got credentials: {}", credentials);
		return credentials;
	}
}
