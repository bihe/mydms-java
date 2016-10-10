package net.binggl.mydms.tags;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import io.dropwizard.hibernate.AbstractDAO;

public class TagDao extends AbstractDAO<Tag> {
    
	@Inject
	public TagDao(final SessionFactory factory) {
        super(factory);
    }

    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Tag save(Tag tag) {
        return persist(tag);
    }
    
    public void delete(Tag tag) {
    	this.currentSession().delete(tag);
    }

    public List<Tag> findAll() {
     	Criteria criteria = this.currentSession().createCriteria(Tag.class);
        return list(criteria.addOrder(Order.asc("name")));
    }
    
    public List<Tag> searchTags(String search) {
     	Criteria criteria = this.currentSession()
     			.createCriteria(Tag.class).add(Restrictions.like("name", search + "%").ignoreCase());
        return list(criteria.addOrder(Order.asc("name")));
    }
}