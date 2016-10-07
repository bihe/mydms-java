package net.binggl.mydms.Example;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;

public class FooDao extends AbstractDAO<Foo> {
    public FooDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Foo> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Foo create(Foo foo) {
        return persist(foo);
    }

    public List<Foo> findAll() {
     	Criteria criteria = this.currentSession().createCriteria(Foo.class);
        return list(criteria);
    }
}