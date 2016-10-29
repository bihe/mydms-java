package net.binggl.mydms.features.documents;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import io.dropwizard.hibernate.UnitOfWork;
import net.binggl.mydms.features.shared.OrderBy;
import net.binggl.mydms.features.shared.SortOrder;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentResource {

	private DocumentStore store;
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy");
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentResource.class);

	@Inject
	public DocumentResource(DocumentStore docStore) {
		this.store = docStore;
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
	 public List<Document> searchDocuments(
			 @QueryParam("title") Optional<String> search,
			 @QueryParam("tag") Optional<Long> byTag,
			 @QueryParam("sender") Optional<Long> bySender,
			 @QueryParam("from") Optional<String> fromDateString,
			 @QueryParam("to") Optional<String> toDateString,
			 @QueryParam("limit") Optional<Integer> limitResults,
			 @QueryParam("skip") Optional<Integer> skipResults
			 ) {
		 
		 Optional<Date> fromDate = Optional.empty();
		 Optional<Date> toDate = Optional.empty();
		 
		 if(fromDateString.isPresent()) {
			 try {
				 fromDate = Optional.of(DateTime.parse(fromDateString.get(), fmt).toDate());	 
			 } catch(Exception EX) {
				 LOGGER.error("Could not parse supplied from-date-string ({}): {}", fromDateString.get(), EX.getMessage());
				 String message = String.format("Could not parse from-date: %s", fromDateString.get());
		         throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
			 }
		 }
		 if(toDateString.isPresent()) {
			 try {
				 DateTime dt = DateTime.parse(toDateString.get(), fmt);
				 DateTime endOfDay = new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 23, 59);
				 toDate = Optional.of(endOfDay.toDate()); 
			 } catch(Exception EX) {
				 LOGGER.error("Could not parse supplied to-date-string ({}): {}", toDateString.get(), EX.getMessage());
				 String message = String.format("Could not parse to-date: %s", toDateString.get());
		         throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
			 }
		 }
		 
		 List<Document> searchResulst = this.store.searchDocuments(search, byTag, bySender, fromDate, toDate, 
				 limitResults, skipResults, new OrderBy("title", SortOrder.Ascending));
		 
		 
		 return searchResulst;
	 }
}