package net.binggl.mydms.features.documents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

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

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import io.dropwizard.hibernate.UnitOfWork;
import net.binggl.mydms.features.documents.models.ActionResult;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.models.DocumentViewModel;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;
import net.binggl.mydms.features.shared.models.NamedItem;
import net.binggl.mydms.features.shared.models.SimpleResult;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource {

	private TagStore tagStore;
	private SenderStore senderStore;
	private DocumentStore store;
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);
	private static final UUID EMPTY = new UUID(0, 0);

	@Inject
	public DocumentResource(DocumentStore docStore, TagStore tagStore, SenderStore senderStore) {
		this.store = docStore;
		this.tagStore = tagStore;
		this.senderStore = senderStore;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<Document> getAll() {
		List<Document> all = this.store.findAll(new OrderBy("title", SortOrder.Ascending),
				new OrderBy("created", SortOrder.Ascending));
		return all;
	}

	@GET
	@Path("search")
	@UnitOfWork
	@Timed
	public List<Document> searchDocuments(@QueryParam("title") Optional<String> search,
			@QueryParam("tag") Optional<Long> byTag, @QueryParam("sender") Optional<Long> bySender,
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

		List<Document> searchResults = this.store.searchDocuments(search, byTag, bySender, fromDate, toDate,
				limitResults, skipResults, new OrderBy("created", SortOrder.Descending),
				new OrderBy("title", SortOrder.Ascending));

		return searchResults;
	}

	@GET
	@UnitOfWork
	@Path("{id}")
	public Document getDocument(@PathParam("id") UUID documentId) {
		LOGGER.debug("Get document {}", documentId);

		Optional<Document> document = store.findById(documentId);
		if (document.isPresent())
			return document.get();

		throw new WebApplicationException(String.format("Could not find the given document (%s)", documentId),
				Status.NOT_FOUND);
	}

	@GET
	@UnitOfWork
	@Path("alt/{id}")
	public Document getDocumentByAlternativeId(@PathParam("id") String alternativeId) {
		LOGGER.debug("Get document by alternative id {}", alternativeId);

		Optional<Document> document = store.findByAlternativeId(alternativeId);
		if (document.isPresent())
			return document.get();

		throw new WebApplicationException(String.format("Could not find the given document (%s)", alternativeId),
				Status.NOT_FOUND);
	}

	@DELETE
	@UnitOfWork
	@Path("{id}")
	public SimpleResult deleteDocument(@PathParam("id") UUID documentId) {
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

			if (docItem.getId() == null || EMPTY.equals(docItem.getId())) {
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
				} else {
					document = this.newIntance();
				}
			}
			document.setTitle(docItem.getTitle());
			document.setFileName(docItem.getFileName());
			document.setAmount(docItem.getAmount());

			List<Tag> tags = this.lookup(docItem.getTags(), item -> {
				Optional<Tag> t = tagStore.tagByName(item.getName());
				if (t.isPresent()) {
					return t;
				} else {
					return Optional.of(new Tag(item.getName()));
				}
			});
			document.getTags().addAll(tags);

			List<Sender> senders = this.lookup(docItem.getSenders(), item -> {
				Optional<Sender> s = senderStore.senderByName(item.getName());
				if (s.isPresent()) {
					return s;
				} else {
					return Optional.of(new Sender(item.getName()));
				}
			});
			document.getSenders().addAll(senders);

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

		} catch (Exception EX) {
			LOGGER.error("Could not save a new document {}", EX.getMessage(), EX);
			String message = "Could not save the document: ";
			message += EX.getMessage();
			throw new WebApplicationException(message, Status.BAD_REQUEST);
		}

		return result;
	}

	private Document newIntance() {
		Document document = new Document();
		document.setId(UUID.randomUUID());
		document.setCreated(new Date());
		document.setModified(null);
		document.setAlternativeId(RandomStringUtils.random(8, true, true));
		document.setPreviewLink(null);
		return document;
	}

	private <V extends NamedItem> List<V> lookup(List<V> items, Function<V, Optional<V>> callback) {
		List<V> lookupList = new ArrayList<>();

		for (V item : items) {
			Optional<V> lookupItem = callback.apply(item);
			if (lookupItem.isPresent())
				lookupList.add(lookupItem.get());
		}
		return lookupList;
	}
}