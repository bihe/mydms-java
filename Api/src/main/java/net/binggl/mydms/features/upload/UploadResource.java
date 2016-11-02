package net.binggl.mydms.features.upload;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import liquibase.util.file.FilenameUtils;
import net.binggl.mydms.config.ApplicationConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.DocumentResource;
import net.binggl.mydms.features.documents.models.ActionResult;
import net.binggl.mydms.features.shared.crypto.HashHelper;

@Path("/upload")
public class UploadResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);
	private ApplicationConfiguration config;

	@Inject
	public UploadResource(MydmsConfiguration configuration) {
		this.config = configuration.getApplication();
	}

	@POST
	@Path("/file/{token}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult uploadFile(@PathParam("token") String token,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("file") final FormDataBodyPart body,
			@HeaderParam("content-length") long contentLength) {

		UploadResult result = new UploadResult("", "", ActionResult.None);

		try {

			if (contentLength > config.getMaxUploadSize()) {
				throw new WebApplicationException(
						String.format("The file exceeds the maximum upload size of %d", config.getMaxUploadSize()),
						Status.BAD_REQUEST);
			}

			String mimeType = body.getMediaType().toString();
			Optional<String> foundMimeType = config.getAllowedFileTypes().stream().filter(item -> mimeType.equals(item))
					.findAny();
			if(!foundMimeType.isPresent()) {
				throw new WebApplicationException(
						String.format("The supplied mime-type is not allowed: %s", mimeType),
						Status.BAD_REQUEST);
			}
			
			String fileToken = generateToken(token, fileDetail.getFileName());
			LOGGER.debug("Will save the given file {} using the created token {}", fileDetail.getFileName(), fileToken);
			
			String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
			java.nio.file.Path outputPath = FileSystems.getDefault().getPath(config.getUploadPath(),
					String.format("%s.%s", fileToken, fileExtension));

			Files.copy(uploadedInputStream, outputPath);
			
			result.setResult(ActionResult.Created);
			result.setToken(fileToken);
			result.setMessage(String.format("File %s was uploaded and stored using token %s", fileDetail.getFileName(), fileToken));

		} catch (Exception EX) {
			LOGGER.error("Coult not upload the given file {}", EX.getMessage(), EX);
			throw new WebApplicationException(String.format("Coult not upload the given file - %s", EX.getMessage()),
					Status.BAD_REQUEST);
		}

		return result;
	}
	
	private String generateToken(String token, String fileName) {
		return HashHelper.getSHA(token, fileName, new Date().toString());
	}
}
