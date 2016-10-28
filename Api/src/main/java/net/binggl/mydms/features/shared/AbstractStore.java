package net.binggl.mydms.features.shared;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import io.dropwizard.hibernate.AbstractDAO;

public abstract class AbstractStore<T> extends AbstractDAO<T> {
    
	private final Class<T> classType;
		
	public AbstractStore(final Class<T> classType, final SessionFactory factory) {
        super(factory);
        this.classType = classType;
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(get(id));
    }
    
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(get(id));
    }

    public T save(T entity) {
        return persist(entity);
    }
    
    public void delete(T entity) {
    	this.currentSession().delete(entity);
    }
    
    public void delete(Long id) {
    	Optional<T> entity = this.findById(id);
    	if(entity.isPresent())
    		this.currentSession().delete(entity.get());
    }
    
    public void delete(UUID id) {
    	Optional<T> entity = this.findById(id);
    	if(entity.isPresent())
    		this.currentSession().delete(entity.get());
    }

    public List<T> findAll() {
     	Criteria criteria = this.currentSession().createCriteria(this.classType);
        return list(criteria.addOrder(Order.asc("name")));
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
