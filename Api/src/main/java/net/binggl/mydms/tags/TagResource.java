package net.binggl.mydms.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {
    
    private TagStore tagDao;
    
    @Inject
	public TagResource(TagStore dao) {
		this.tagDao = dao;
	}
    
    @GET
    @UnitOfWork
    @Timed
    public List<Tag> getAll() {
        List<Tag> allTags = this.tagDao.findAll();
        return allTags;
    }
    
    @GET
    @Path("search")
    @UnitOfWork
    @Timed
    public List<Tag> searchTags(@QueryParam("name") Optional<String> search) {
    	String searchFor = search.orElse("");
        List<Tag> tags = new ArrayList<Tag>();
        if(!"".equals(searchFor)) {
        	tags = this.tagDao.searchTags(searchFor);
        } else {
        	tags = this.tagDao.findAll();
        }
        
        return tags;
    }
}