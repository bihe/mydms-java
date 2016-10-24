package net.binggl.mydms.shared;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import io.dropwizard.hibernate.AbstractDAO;

public abstract class AbstractStore<T> extends AbstractDAO<T> {
    
	private final Class<T> classType;
	private static final String FIELD_NAME = "name";
	
	public AbstractStore(final Class<T> classType, final SessionFactory factory) {
        super(factory);
        this.classType = classType;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public T save(T entity) {
        return persist(entity);
    }
    
    public void delete(T entity) {
    	this.currentSession().delete(entity);
    }

    public List<T> findAll() {
     	Criteria criteria = this.currentSession().createCriteria(this.classType);
        return list(criteria.addOrder(Order.asc("name")));
    }
    
    public List<T> searchByName(String search) {
     	Criteria criteria = this.currentSession()
     			.createCriteria(this.classType).add(Restrictions.like(FIELD_NAME, search + "%").ignoreCase());
        return list(criteria.addOrder(Order.asc(FIELD_NAME)));
    }
    
    public boolean any() {
    	Criteria crit = this.currentSession().createCriteria(this.classType);
		boolean anyItemAvailable = false;
		Criteria criteria = crit.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		anyItemAvailable = (count != null && count > 0);
		return anyItemAvailable;
	}
}
