package net.binggl.mydms.features.shared.store;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import io.dropwizard.hibernate.AbstractDAO;

public abstract class AbstractHibernateStore<T> extends AbstractDAO<T> {

	private final Class<T> classType;

	public AbstractHibernateStore(final Class<T> classType, final SessionFactory factory) {
		super(factory);
		this.classType = classType;
	}

	public Optional<T> findById(Long id) {
		return Optional.ofNullable(get(id));
	}

	public Optional<T> findById(String id) {
		return Optional.ofNullable(get(id));
	}
	
	public void flush() {
		this.currentSession().flush();
	}

	public T save(T entity) {
		return persist(entity);
	}

	public void delete(T entity) {
		this.currentSession().delete(entity);
	}

	public void delete(Long id) {
		Optional<T> entity = this.findById(id);
		if (entity.isPresent())
			this.currentSession().delete(entity.get());
	}

	public void delete(String id) {
		Optional<T> entity = this.findById(id);
		if (entity.isPresent())
			this.currentSession().delete(entity.get());
	}

	public List<T> findAll(OrderBy... order) {
		Criteria criteria = this.currentSession().createCriteria(this.classType);
		for (OrderBy o : order) {
			if (o.getSort() == SortOrder.Ascending)
				criteria = criteria.addOrder(Order.asc(o.getField()));
			else
				criteria = criteria.addOrder(Order.desc(o.getField()));
		}
		return list(criteria);
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
