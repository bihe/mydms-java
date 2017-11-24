package net.binggl.mydms.features.documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.binggl.mydms.features.documents.viewmodels.PagedDocuments;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import io.dropwizard.hibernate.UnitOfWork;
import liquibase.util.file.FilenameUtils;
import net.binggl.mydms.config.ApplicationConfiguration;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.models.DocumentsSenders;
import net.binggl.mydms.features.documents.models.DocumentsTags;
import net.binggl.mydms.features.documents.viewmodels.DocumentViewModel;
import net.binggl.mydms.features.files.FileItem;
import net.binggl.mydms.features.files.FileService;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;
import net.binggl.mydms.features.shared.models.ActionResult;
import net.binggl.mydms.features.shared.models.NamedItem;
import net.binggl.mydms.features.shared.models.SimpleResult;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;
import net.binggl.mydms.features.upload.UploadStore;
import net.binggl.mydms.features.upload.models.UploadItem;

@Path("/api/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("User")
public class DocumentResource {

	private TagStore tagStore;
	private SenderStore senderStore;
	private DocumentStore store;
	private UploadStore uploadStore;
	private FileService fileService;
	private ApplicationConfiguration config;
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);
	private static final UUID EMPTY = new UUID(0, 0);

	@Inject
	public DocumentResource(DocumentStore docStore, TagStore tagStore, SenderStore senderStore, UploadStore uploadStore,
			FileService fileService, MydmsConfiguration configuration) {
		this.store = docStore;
		this.tagStore = tagStore;
		this.senderStore = senderStore;
		this.fileService = fileService;
		this.uploadStore = uploadStore;
		this.config = configuration != null ? configuration.getApplication() : null;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<DocumentViewModel> getAll() {
		return this.store.findAllItems(
				new OrderBy("created", SortOrder.Descending),
				new OrderBy("title", SortOrder.Ascending));
	}
	
	@GET
	@Path("search")
	@UnitOfWork
	@Timed
	public PagedDocuments searchDocuments(@QueryParam("title") Optional<String> search,
			@QueryParam("tag") Optional<String> byTag, @QueryParam("sender") Optional<String> bySender,
			@QueryParam("from") Optional<String> fromDateString, @QueryParam("to") Optional<String> toDateString,
			@QueryParam("limit") Optional<Integer> limitResults, @QueryParam("skip") Optional<Integer> skipResults) {

		Optional<Date> fromDate = Optional.empty();
		Optional<Date> toDate = Optional.empty();

		if (fromDateString.isPresent()) {
			try {
				fromDate = Optional.of(DateTime.parse(fromDateString.get(), fmt).toDate());
			} catch (Exception EX) {
				LOGGER.error("Could not parse supplied from-date-string ({}): {}", fromDateString.get(),
						EX.getMessage());
				String message = String.format("Could not parse from-date: %s", fromDateString.get());
				throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
			}
		}
		if (toDateString.isPresent()) {
			try {
				DateTime dt = DateTime.parse(toDateString.get(), fmt);
				DateTime endOfDay = new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 23, 59);
				toDate = Optional.of(endOfDay.toDate());
			} catch (Exception EX) {
				LOGGER.error("Could not parse supplied to-date-string ({}): {}", toDateString.get(), EX.getMessage());
				String message = String.format("Could not parse to-date: %s", toDateString.get());
				throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
			}
		}

		PagedDocuments searchResults = this.store.searchDocuments(search, byTag, bySender, fromDate, toDate,
				limitResults, skipResults, new OrderBy("created", SortOrder.Descending),
				new OrderBy("title", SortOrder.Ascending));

		return searchResults;
	}
	
	@GET
	@UnitOfWork
	@Path("{id}")
	public DocumentViewModel getDocument(@PathParam("id") String documentId) {
		LOGGER.debug("Get document {}", documentId);

		Optional<Document> document = store.findById(documentId);
		if(!document.isPresent())
			throw new WebApplicationException(String.format("Could not find the given document (%s)", documentId),
					Status.NOT_FOUND);
		
		return this.toModel(document);
	}

	@GET
	@UnitOfWork
	@Path("alt/{id}")
	public DocumentViewModel getDocumentByAlternativeId(@PathParam("id") String alternativeId) {
		LOGGER.debug("Get document by alternative id {}", alternativeId);

		Optional<Document> document = store.findByAlternativeId(alternativeId);
		if (!document.isPresent())
			throw new WebApplicationException(String.format("Could not find the given document (%s)", alternativeId),
					Status.NOT_FOUND);

		return this.toModel(document);
	}
	
	@DELETE
	@UnitOfWork
	@Path("{id}")
	public SimpleResult deleteDocument(@PathParam("id") String documentId) {
		SimpleResult result = new SimpleResult("", ActionResult.None);

		LOGGER.debug("Delete document {}", documentId);

		Optional<Document> document = store.findById(documentId);
		if (document.isPresent()) {
			store.delete(document.get());

			result.setMessage(String.format("Document '%s' was deleted!", documentId));
			result.setResult(ActionResult.Deleted);

			return result;
		}

		throw new WebApplicationException(String.format("Could not find the given document (%s)", documentId),
				Status.NOT_FOUND);
	}
	
	@POST
	@UnitOfWork
	@Timed
	public SimpleResult saveDocument(@NotNull @Valid DocumentViewModel docItem) {
		SimpleResult result = new SimpleResult("", ActionResult.None);
		boolean newDoc = true;
		try {
			Document document = null;

			if (docItem.getId() == null || EMPTY.toString().equals(docItem.getId())) {
				LOGGER.debug("Will create a new document useing {}", docItem);
				document = this.newIntance();
			} else {
				LOGGER.debug("Lookup the existing document {}", docItem.getId());

				Optional<Document> lookupDocument = store.findById(docItem.getId());
				if (lookupDocument.isPresent()) {
					document = lookupDocument.get();
					document.setModified(new Date());
					document.getTags().clear();
					document.getSenders().clear();
					newDoc = false;
					
					this.store.flush(); // force a flush to delete the tags/senders
					
				} else {
					document = this.newIntance();
				}
			}
			document.setTitle(Encode.forHtml(docItem.getTitle()));
			document.setFileName(Encode.forHtml(docItem.getFileName()));
			document.setAmount(docItem.getAmount());

			List<Tag> tags = this.lookup(docItem.getTags(), item -> {
				Optional<Tag> t = tagStore.tagByName(Encode.forHtml(item));
				if (t.isPresent()) {
					return t;
				} else {
					Tag tag = new Tag(Encode.forHtml(item));
					this.tagStore.save(tag);
					return Optional.of(tag);
				}
			});
			for(Tag t : tags) {
				DocumentsTags ref = new DocumentsTags(document, t);
				document.getTags().add(ref);
			}

			List<Sender> senders = this.lookup(docItem.getSenders(), item -> {
				Optional<Sender> s = senderStore.senderByName(Encode.forHtml(item));
				if (s.isPresent()) {
					return s;
				} else {
					Sender sender = new Sender(Encode.forHtml(item));
					this.senderStore.save(sender);
					return Optional.of(sender);
				}
			});
			for(Sender s : senders) {
				DocumentsSenders ref = new DocumentsSenders(document, s);
				document.getSenders().add(ref);
			}

			// use the uploadFileToken and retrieve the upload-queue-item
			String filePath = this.processUploadedFile(docItem.getUploadFileToken(), document.getFileName());
			if(StringUtils.isEmpty(filePath)) {
				throw new WebApplicationException("Could not process upload file in backend!");
			}
			document.setFileName(filePath);
						
			Document saved = store.save(document);
			if (saved != null) {
				String message = "";
				if (newDoc) {
					LOGGER.info("Created new document '{}' ({})", saved.getTitle(), saved.getId());
					message = String.format("New document created '%s' (%s)", saved.getTitle(), saved.getId());
				} else {
					LOGGER.info("Updated existing document '{}' ({})", saved.getTitle(), saved.getId());
					message = String.format("Updated existing document '%s' (%s)", saved.getTitle(), saved.getId());
				}
				result = new SimpleResult(message, ActionResult.Created);
			}
		} catch (ConstraintViolationException cvEX) {
			LOGGER.error("Could not save a new document {}", cvEX.getMessage());
			String message = "The supplied information were not valid!";
			throw new WebApplicationException(message, Status.BAD_REQUEST);
		} catch (WebApplicationException webEx) {
			LOGGER.error("Could not save a new document {}", webEx.getMessage(), webEx);
			throw webEx;
		} catch (Exception EX) {
			LOGGER.error("Could not save a new document {}", EX.getMessage(), EX);
			String message = "Could not save the document: ";
			message += EX.getMessage();
			throw new WebApplicationException(message, Status.BAD_REQUEST);
		}

		return result;
	}

	
	
	
	private String processUploadedFile(String uploadToken, String fileName) throws IOException {
		String filePath = "";
		if (StringUtils.isEmpty(uploadToken) || "-".equals(uploadToken))
			return fileName;

		Optional<UploadItem> upload = uploadStore.findById(uploadToken);
		if (!upload.isPresent()) {
			throw new WebApplicationException(String.format("The given upload token %s is not available!", uploadToken),
					Status.BAD_REQUEST);
		}

		LOGGER.debug("Got upload-item from store!");

		// folder is current date
		DateTimeFormatter folderFormat = DateTimeFormat.forPattern("yyyy_MM_dd");
		String folderName = DateTime.now().toString(folderFormat);

		// 1) upload the file to the google store
		File uploadFile = this.getFile(fileName, config.getUploadPath(), uploadToken);
		byte[] payload = FileUtils.readFileToByteArray(uploadFile);
		if (payload == null) {
			throw new WebApplicationException(
					String.format("There is no upload file payload available for token %s", uploadToken),
					Response.Status.BAD_GATEWAY);
		}

		LOGGER.debug("Read file {}", uploadFile.getName());

		FileItem item = new FileItem(fileName, upload.get().getMimeType(), payload, folderName);

		if (!this.fileService.saveFile(item)) {
			throw new WebApplicationException(String.format("Could not upload file: %s!", item.getFileName()),
					Response.Status.BAD_GATEWAY);
		}
		
		filePath = String.format("/%s/%s", folderName, item.getFileName());

		LOGGER.debug("Saved file to backend file-store!");

		// 2) clear the temp file
		if (!uploadFile.delete()) {
			LOGGER.warn(String.format("Could not delete upload file on filesystem! %s", uploadToken));
		}

		// 3) remove the uploadItem from the database
		this.uploadStore.delete(upload.get().getId());

		return filePath;
	}

	private File getFile(String fileName, String uploadPath, String uploadToken) {
		String fileExtension = FilenameUtils.getExtension(fileName);
		java.nio.file.Path outputPath = FileSystems.getDefault().getPath(uploadPath,
				String.format("%s.%s", uploadToken, fileExtension));
		return outputPath.toFile();
	}

	private Document newIntance() {
		Document document = new Document();
		document.setId(UUID.randomUUID().toString());
		document.setCreated(new Date());
		document.setModified(null);
		document.setAlternativeId(RandomStringUtils.random(8, true, true));
		document.setPreviewLink(null);
		return document;
	}

	private <V extends NamedItem> List<V> lookup(List<String> items, Function<String, Optional<V>> callback) {
		List<V> lookupList = new ArrayList<>();

		for (String item : items) {
			Optional<V> lookupItem = callback.apply(item);
			if (lookupItem.isPresent())
				lookupList.add(lookupItem.get());
		}
		return lookupList;
	}
	
	private DocumentViewModel toModel(Optional<Document> document) {
		DocumentViewModel model = new DocumentViewModel();
		if (document.isPresent()) {
			model.setId(document.get().getId());
			model.setAlternativeId(document.get().getAlternativeId());
			model.setAmount(document.get().getAmount());
			model.setCreated(document.get().getCreated());
			model.setFileName(document.get().getFileName());
			model.setModified(document.get().getModified());
			model.setTitle(document.get().getTitle());
			
			model.setTags(document.get().getTags().stream().map(a -> a.getTag().getName()).collect(Collectors.toList()));
			model.setSenders(document.get().getSenders().stream().map(a -> a.getSender().getName()).collect(Collectors.toList()));
		}
		
		return model;
	}
}