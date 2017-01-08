package net.binggl.mydms.features.upload;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

import io.dropwizard.hibernate.UnitOfWork;
import liquibase.util.file.FilenameUtils;
import net.binggl.mydms.config.ApplicationConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.DocumentResource;
import net.binggl.mydms.features.shared.models.ActionResult;
import net.binggl.mydms.features.upload.models.UploadItem;
import net.binggl.mydms.features.upload.models.UploadResult;

@Path("/api/upload")
@RolesAllowed("User")
public class UploadResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);
	private ApplicationConfiguration config;
	private UploadStore store;

	@Inject
	public UploadResource(MydmsConfiguration configuration, UploadStore store) {
		this.config = configuration.getApplication();
		this.store = store;
	}

	@POST
	@Path("/file")
	@UnitOfWork
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public UploadResult uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
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
			
			String id = UUID.randomUUID().toString();
			UploadItem uploadQueueItem = new UploadItem(id, fileDetail.getFileName(), mimeType);
			
			LOGGER.debug("Will save the given file {} using the created token {}", fileDetail.getFileName(), id.toString());
			
			String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
			java.nio.file.Path outputPath = FileSystems.getDefault().getPath(config.getUploadPath(),
					String.format("%s.%s", id.toString(), fileExtension));

			Files.copy(uploadedInputStream, outputPath);
			
			if(store.save(uploadQueueItem) != null) {
				result.setResult(ActionResult.Created);
				result.setToken(id.toString());
				result.setMessage(String.format("File %s was uploaded and stored using token %s", fileDetail.getFileName(), id.toString()));
			} else {
				LOGGER.error("Could not store upload file in database!");
				throw new WebApplicationException(String.format("Could not store upload file in database!"), Status.BAD_REQUEST);
			}
		} catch (Exception EX) {
			LOGGER.error("Could not upload the given file {}", EX.getMessage(), EX);
			throw new WebApplicationException(String.format("Coult not upload the given file - %s", EX.getMessage()),
					Status.BAD_REQUEST);
		}

		return result;
	}
}
