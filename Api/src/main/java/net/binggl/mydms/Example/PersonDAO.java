package net.binggl.mydms.Example;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;

public class PersonDAO extends AbstractDAO<Person> {
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Person create(Person person) {
        return persist(person);
    }

    public List<Person> findAll() {
        return list(namedQuery("net.binggl.mydms.Example.Person.findAll"));
    }
}