package net.binggl.mydms.Example;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    
    private final PersonDAO peopleDAO;
    private final FooDao fooDAO;

    public ExampleResource(String template, String defaultName, PersonDAO dao, FooDao fooDAO) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        this.peopleDAO = dao;
        this.fooDAO = fooDAO;
    }

    
    @GET
    @UnitOfWork
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        
        List<Person> allPeople = this.peopleDAO.findAll();
        List<Foo> allFoos = this.fooDAO.findAll();
        
        return new Saying(counter.incrementAndGet(), value, allPeople, allFoos);
    }
}