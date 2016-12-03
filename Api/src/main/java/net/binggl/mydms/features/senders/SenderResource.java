package net.binggl.mydms.features.senders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/senders")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("User")
public class SenderResource {

	private SenderStore store;

	@Inject
	public SenderResource(SenderStore store) {
		this.store = store;
	}

	@GET
	@UnitOfWork
	@Timed
	public List<Sender> getAll() {
		List<Sender> allSenders = this.store.findAll();
		return allSenders;
	}

	@GET
	@Path("search")
	@UnitOfWork
	@Timed
	public List<Sender> searchSenders(@QueryParam("name") Optional<String> search) {
		String searchFor = search.orElse("");
		List<Sender> senders = new ArrayList<Sender>();
		if (!"".equals(searchFor)) {
			senders = this.store.searchByName(searchFor);
		} else {
			senders = this.store.findAll();
		}

		return senders;
	}
}