package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.DocumentEntity
import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import net.binggl.mydms.features.records.model.SortOrder
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
        var queryString = "select d from DocumentEntity d "
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
        queryString += " " + this.getDocumentOrderBy("d", *order)

        val jpaQuery = this.entityManager.createQuery(queryString, DocumentEntity::class.java)
        val jpaCountQuery = this.entityManager.createQuery(queryStringCount)


        // set the query parameters

        if (title.isPresent) {
            jpaQuery.setParameter("title", "%${title.get().toLowerCase()}%")
            jpaCountQuery.setParameter("title", "%${title.get().toLowerCase()}%")
        }
        if (dateFrom.isPresent) {
            jpaQuery.setParameter("dateFrom", dateFrom.get())
            jpaCountQuery.setParameter("dateFrom", dateFrom.get())
        }
        if (dateUntil.isPresent) {
            jpaQuery.setParameter("dateUntil", dateUntil.get())
            jpaCountQuery.setParameter("dateUntil", dateUntil.get())
        }
        if (tag.isPresent) {
            jpaQuery.setParameter("tag", tag.get().toLowerCase())
            jpaCountQuery.setParameter("tag", tag.get().toLowerCase())
        }
        if (sender.isPresent) {
            jpaQuery.setParameter("sender", sender.get().toLowerCase())
            jpaCountQuery.setParameter("sender", sender.get().toLowerCase())
        }

        // paging
        if (limit.isPresent) {
            jpaQuery.maxResults = limit.get()
        }
        if (skip.isPresent) {
            jpaQuery.firstResult = skip.get()
        }

        val count = jpaCountQuery.singleResult // get the total number of results
        val result = jpaQuery.resultList

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
                    previewLink = it.previewLink,
                    fileName = it.fileName
            )}
                , totalEntries = count as Long)
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