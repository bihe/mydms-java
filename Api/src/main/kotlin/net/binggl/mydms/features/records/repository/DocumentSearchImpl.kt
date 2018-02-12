package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.DocumentEntity
import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.shared.util.toBase64
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManager

internal class DocumentSearchImpl : DocumentSearch {

    @Autowired
    private lateinit var entityManager: EntityManager

    /* searchDocuments search using a number of fields in the database
       a bunch of optional parameters are supplied so the query needs to
       use some intelligence.
       the result is not a plain entity but a view model for the client layer
     */
    override fun searchDocuments(title: Optional<String>, tag: Optional<String>,
                                 sender: Optional<String>, dateFrom: Optional<LocalDateTime>,
                                 dateUntil: Optional<LocalDateTime>, limit: Optional<Int>,
                                 skip: Optional<Int>, vararg order: OrderBy): PagedDocuments {

        val whereClause = StringBuilder()
        val joinStatement = StringBuilder()
        val whereDefault = " where 1=1 "
        val orderBy = this.getDocumentOrderBy("d", *order)
        var queryString = "select d.id from DocumentEntity d "
        var queryStringCount = "select count(d.id) from DocumentEntity d "
        var hasWhere = false
        var hasJoin = false


        if (title.isPresent) {
            whereClause.append(" and ( lower(d.title) LIKE :title or lower(d.tagList) like :title or lower(d.senderList) like :title )")
            hasWhere = true
        }

        if (dateFrom.isPresent) {
            whereClause.append(" and d.created >= :dateFrom")
            hasWhere = true
        }

        if (dateUntil.isPresent) {
            whereClause.append(" and d.created <= :dateUntil")
            hasWhere = true
        }

        if (tag.isPresent) {
            joinStatement.append(" inner join d.tags t ")
            whereClause.append(" and lower(t.name) = :tag")
            hasWhere = true
            hasJoin = true
        }

        if (sender.isPresent) {
            joinStatement.append(" inner join d.senders s ")
            whereClause.append(" and lower(s.name) = :sender")
            hasWhere = true
            hasJoin = true
        }

        if (hasJoin) {
            queryString += joinStatement.toString()
            queryStringCount += joinStatement.toString()
        }

        if (hasWhere) {
            queryString += whereDefault + whereClause.toString()
            queryStringCount += whereDefault + whereClause.toString()
        }

        // orderBy
        queryString += " " + orderBy

        val jpaIdQuery = this.entityManager.createQuery(queryString, String::class.java)
        val jpaCountQuery = this.entityManager.createQuery(queryStringCount)


        // set the query parameters

        if (title.isPresent) {
            jpaIdQuery.setParameter("title", "%${title.get().toLowerCase()}%")
            jpaCountQuery.setParameter("title", "%${title.get().toLowerCase()}%")
        }
        if (dateFrom.isPresent) {
            jpaIdQuery.setParameter("dateFrom", dateFrom.get())
            jpaCountQuery.setParameter("dateFrom", dateFrom.get())
        }
        if (dateUntil.isPresent) {
            jpaIdQuery.setParameter("dateUntil", dateUntil.get())
            jpaCountQuery.setParameter("dateUntil", dateUntil.get())
        }
        if (tag.isPresent) {
            jpaIdQuery.setParameter("tag", tag.get().toLowerCase())
            jpaCountQuery.setParameter("tag", tag.get().toLowerCase())
        }
        if (sender.isPresent) {
            jpaIdQuery.setParameter("sender", sender.get().toLowerCase())
            jpaCountQuery.setParameter("sender", sender.get().toLowerCase())
        }

        // paging
        if (limit.isPresent) {
            jpaIdQuery.maxResults = limit.get()
        }
        if (skip.isPresent) {
            jpaIdQuery.firstResult = skip.get()
        }

        //
        // the logic performs 3 queries in fact
        // 1) count the number of entries for the supplied parameters
        // 2) get a list of ids for the given query (including the filtering parameters)
        // 3) retrieve the entity/objects for the given ids
        //
        // why three queries? the reason is lazy loading of hibernate related relations.
        // the data-structure contains a M:N relation to tags and to senders.
        // the query returns documents objects which have a list of tags/senders as a property.
        //
        // in the default lazy-loading setup of hibernate once the tags/senders collection is
        // accessed additional queries to retrieve tags/senders would be executed (on demand, lazily loaded).
        // This would lead to the following number of queries in total (e.g. for 5 document entries):
        //      1 count query
        //      1 query for documents
        //      10 queries for tags / senders
        //          per document entity
        //              1 query for the tags relation
        //              1 query for the senders relation
        // 12 queries in total
        //
        // The obvious solution to limit the number of queries would be to FETCH the related entities. This
        // can be done one a model level @see FetchType (https://www.thoughts-on-java.org/entity-mappings-introduction-jpa-fetchtypes/)
        // or by using the JOIN FETCH syntax
        //
        // One problem occurs when using the FETCH syntax in combination with setMaxResults (aka paging).
        // setMaxResults only limits (like SQL LIMIT) the total number of rows. If we have an eager fetch - a join
        // to the related values happens, which increases the total number of "rows" returned by the query. Think
        // of a SQL JOIN - you get more rows for M:N, 1:N relations. Hibernate deals with the mapping of the owner
        // objects and the related objects. But setMaxResults only works on the pure DB rows.
        //
        // The hibernate solution for this is to fetch all entries - and perform the logic/magic of setMaxResults in
        // memory. Definitely not a desired approach for large datasets.
        //
        // Different solutions are possible - for me the simplest would be to apply setMaxResults to a query returning
        // ids of objects. The JOIN FETCH would then be applied to another query. This results in a additional query,
        // but this is a good trade off compared the the default lazy-loading, or doing paging in memory.
        //
        // credit to: https://www.tikalk.com/java/blog/hibernate-setmaxresults-join-tables/
        //

        val count = jpaCountQuery.singleResult // get the total number of results
        val resultIds = jpaIdQuery.resultList

        // fetch the real document entries for the given ids
        // https://stackoverflow.com/questions/18753245/one-to-many-relationship-gets-duplicate-objects-whithout-using-distinct-why
        val entityQueryString = "select distinct d from DocumentEntity d join fetch d.tags t join fetch d.senders s where d.id in :idList $orderBy"
        val jpaEntityQuery = this.entityManager.createQuery(entityQueryString, DocumentEntity::class.java)
        jpaEntityQuery.setParameter("idList", resultIds)
        val result = jpaEntityQuery.resultList

        return PagedDocuments(documents = result.map {
            Document(
                    id = it.id,
                    title = it.title,
                    created = it.created,
                    alternativeId = it.alternativeId,
                    tags = it.tags.map { it.name },
                    modified = it.modified,
                    amount = it.amount,
                    uploadFileToken = null,
                    senders = it.senders.map { it.name },
                    previewLink = it.previewLink ?: it.fileName.toBase64(),
                    fileName = it.fileName
            )}, totalEntries = count as Long)
    }


    private fun getDocumentOrderBy(tableAlias: String, vararg order: OrderBy): String {
        if (order.isEmpty())
            return ""

        val orderFields = StringBuffer(" order by ")
        for ((index, orderBy) in order.withIndex()) {
            if (index > 0)
                orderFields.append(",")

            if (orderBy.sort === SortOrder.Ascending) {
                orderFields.append(" $tableAlias.${orderBy.field} ASC")
            } else {
                orderFields.append(" $tableAlias.${orderBy.field} DESC")
            }
        }
        return orderFields.toString()
    }
}
