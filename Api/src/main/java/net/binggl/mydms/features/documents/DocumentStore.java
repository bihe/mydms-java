package net.binggl.mydms.features.documents;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.binggl.mydms.features.documents.viewmodels.PagedDocuments;
import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.viewmodels.DocumentViewModel;
import net.binggl.mydms.features.shared.store.AbstractHibernateStore;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;

public class DocumentStore extends AbstractHibernateStore<Document> {

	@Inject
	public DocumentStore(final SessionFactory factory) {
		super(Document.class, factory);
	}

	@SuppressWarnings("unchecked")
	public PagedDocuments searchDocuments(Optional<String> title, Optional<String> tag, Optional<String> sender,
			Optional<Date> dateFrom, Optional<Date> dateUntil, Optional<Integer> limit, Optional<Integer> skip,
			OrderBy... order) {

        PagedDocuments documents = new PagedDocuments();

		StringBuffer sqlQuery = new StringBuffer("SELECT %SELECT% FROM DOCUMENTS d %JOIN% %WHERE%");
		StringBuffer sqlJoin = new StringBuffer();
		boolean hasWhere = false;
		boolean hasJoin = false;
		
		// query
		
		if (title.isPresent()) {
			sqlQuery.append(" AND ( lower(d.title) LIKE :title OR lower(d.taglist) LIKE :title OR lower(d.senderlist) LIKE :title ) ");
			hasWhere = true;
		}
		if (tag.isPresent()) {
			sqlJoin.append(" INNER JOIN DOCUMENTS_TO_TAGS dt ON d.id = dt.document_id INNER JOIN TAGS t ON dt.tag_id = t.id ");
			sqlQuery.append(" AND t.name = :tagName ");
			hasJoin = true;
			hasWhere = true;
		}
		if (sender.isPresent()) {
			sqlJoin.append(" INNER JOIN DOCUMENTS_TO_SENDERS ds ON d.id = ds.document_id INNER JOIN SENDERS s ON ds.sender_id = s.id ");
			sqlQuery.append(" AND s.name = :senderName ");
			hasJoin = true;
			hasWhere = true;
		}
		if (dateFrom.isPresent()) {
			sqlQuery.append(" AND d.created >= :dateFrom");
			hasWhere = true;
		}
		if (dateUntil.isPresent()) {
			sqlQuery.append(" AND d.created <= :dateUntil");
			hasWhere = true;
		}

		sqlQuery = sqlQuery.append("%ORDERBY%");
		String sql = sqlQuery.toString();
		
		if(hasWhere) {
			sql = sql.replace("%WHERE%", " WHERE 1 = 1 ");
		} else {
			sql = sql.replace("%WHERE%", "");
		}
		
		if(hasJoin) {
			sql = sql.replace("%JOIN%", sqlJoin.toString());
		} else {
			sql = sql.replace("%JOIN%", "");
		}

		String fullSQL = sql.replaceAll("%SELECT%","d.*");
		fullSQL = fullSQL.replaceAll("%ORDERBY%", this.getOrderByClause(order));

		String countSQL = sql.replaceAll("%SELECT%","count(d.id)");
		countSQL = countSQL.replaceAll("%ORDERBY%", "");

		Query queryCount = this.currentSession().createNativeQuery(countSQL);
		Query query = this.currentSession().createNativeQuery(fullSQL)
				.setResultTransformer(new DocumentViewModelResultTransformer());
		
		// parameters
		
		if (title.isPresent()) {
			query.setParameter("title", String.format("%%%s%%", title.get().toLowerCase()));
            queryCount.setParameter("title", String.format("%%%s%%", title.get().toLowerCase()));
		}
		if (tag.isPresent()) {
			query.setParameter("tagName", tag.get());
            queryCount.setParameter("tagName", tag.get());
		}
		if (sender.isPresent()) {
			query.setParameter("senderName", sender.get());
            queryCount.setParameter("senderName", sender.get());
		}
		if (dateFrom.isPresent()) {
			query.setParameter("dateFrom", dateFrom.get());
            queryCount.setParameter("dateFrom", dateFrom.get());
		}
		if (dateUntil.isPresent()) {
			query.setParameter("dateUntil", dateUntil.get());
            queryCount.setParameter("dateUntil", dateUntil.get());
		}

		Number numberOfEntries = (Number) queryCount.getSingleResult();
        documents.setTotalEntries(numberOfEntries.longValue());

		// post
		
		if (limit.isPresent()) {
			query = query.setMaxResults(limit.get());
		}
		if (skip.isPresent()) {
			query = query.setFirstResult(skip.get());
		}

		documents.setDocuments((List<DocumentViewModel>)query.list());

		return documents;
	}

	@Override
	public Document save(Document entity) {
		
		if(entity.getTags() != null) {
			String tagList = null;
			tagList = entity.getTags().stream()
					.map(a -> a.getTag().getName())
					.collect(Collectors.joining(";"));
			entity.setTagList(tagList);
		}
		
		if(entity.getSenders() != null) {
			String senderList = null;
			senderList = entity.getSenders().stream()
					.map(a -> a.getSender().getName())
					.collect(Collectors.joining(";"));
			entity.setSenderList(senderList);
		}
		
		return persist(entity);
	}
	
	public List<DocumentViewModel> findAllItems(OrderBy... order) {
		
		String sqlQuery = "SELECT d.* FROM DOCUMENTS d ";
		sqlQuery += this.getOrderByClause(order);
				
		Query query = this.currentSession().createNativeQuery(sqlQuery)
						.setResultTransformer(new DocumentViewModelResultTransformer());
		
		@SuppressWarnings("unchecked")
		List<DocumentViewModel> result = (List<DocumentViewModel>)query.list();
		
		return result;
	}
	
	public Optional<Document> findByAlternativeId(String alternativeId) {
		Criteria criteria = this.currentSession().createCriteria(Document.class)
				.add(Restrictions.eq("alternativeId", alternativeId).ignoreCase());
		return Optional.ofNullable(uniqueResult(criteria));
	}
	
	private String getOrderByClause(OrderBy... order) {
		if(order == null || order.length == 0)
			return "";
		
		StringBuffer orderFields = new StringBuffer(" ORDER BY ");
		int i=0;
		for(OrderBy o : order) {
			if(i > 0)
				orderFields.append(",");
			
			if(o.getSort() == SortOrder.Ascending) {
				orderFields.append(" d." + o.getField() + " ASC");
			} else {
				orderFields.append(" d." + o.getField() + " DESC");
			}
			++i;
		}
		return orderFields.toString();
	}

}