package net.binggl.mydms.features.records.data

import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.infrastructure.error.MydmsException
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class JdbcDocumentStore(private val jdbcT: NamedParameterJdbcTemplate) : DocumentStore {

    override fun findById(id: String): Optional<Document> {
        if (StringUtils.isEmpty(id)) {
            throw MydmsException("Cannot lookup a document with empty id!")
        }

        val result = this.jdbcT.query("SELECT id,title,filename,alternativeid,previewlink," +
                "amount,created,modified,taglist,senderlist FROM DOCUMENTS WHERE id = :id",
                MapSqlParameterSource("id", id), { rs, _ ->
            Document(id = rs.getString("id"),
                    title = rs.getString("title"),
                    fileName = rs.getString("filename"),
                    alternativeId = rs.getString("alternativeid"),
                    previewLink = rs.getString("previewlink"),
                    amount = rs.getDouble("amount"),
                    created = null, //rs.getTimestamp("created"),
                    modified = null, //rs.getTimestamp("modified"),
                    tags = rs.getString("taglist")?.split(";") ?: emptyList(),
                    senders = rs.getString("senderlist")?.split(";") ?: emptyList())
        })

        if (result.isEmpty()) {
            return Optional.empty()
        }
        return Optional.of(result[0])
    }

    override fun searchDocuments(title: Optional<String>, tag: Optional<String>, sender: Optional<String>,
                                 dateFrom: Optional<Date>, dateUntil: Optional<Date>, limit: Optional<Int>,
                                 skip: Optional<Int>, vararg order: OrderBy): PagedDocuments {

        val baseQuery = "SELECT %SELECT% FROM DOCUMENTS %JOIN% %WHERE%"
        val sqlQuery = StringBuffer(baseQuery)
        val sqlJoin = StringBuffer()
        val columns = "DOCUMENTS.id,DOCUMENTS.title,DOCUMENTS.filename,DOCUMENTS.alternativeid,DOCUMENTS.previewlink,DOCUMENTS.amount,DOCUMENTS.created,DOCUMENTS.modified,DOCUMENTS.taglist,DOCUMENTS.senderlist"
        val count = "COUNT(id)"

        var hasWhere = false
        var hasJoin = false

        // based on the supplied parameters add SQL to the query
        val parameters = MapSqlParameterSource()

        if (title.isPresent) {
            sqlQuery.append(" AND ( lower(DOCUMENTS.title) LIKE :title OR lower(DOCUMENTS.taglist) LIKE :title OR lower(DOCUMENTS.senderlist) LIKE :title ) ")
            hasWhere = true
            parameters.addValue("title", "%${title.get().toLowerCase()}%")
        }

        if (tag.isPresent) {
            sqlJoin.append(" INNER JOIN DOCUMENTS_TO_TAGS ON DOCUMENTS.id = DOCUMENTS_TO_TAGS.document_id INNER JOIN TAGS on TAGS.id = DOCUMENTS_TO_TAGS.tag_id ")
            sqlQuery.append(" AND TAGS.name = :tagName ")
            hasJoin = true
            hasWhere = true
            parameters.addValue("tagName", tag.get())
        }

        if (sender.isPresent) {
            sqlJoin.append(" INNER JOIN DOCUMENTS_TO_SENDERS ON DOCUMENTS.id = DOCUMENTS_TO_SENDERS.document_id INNER JOIN SENDERS on SENDERS.id = DOCUMENTS_TO_SENDERS.sender_id ")
            sqlQuery.append(" AND SENDERS.name = :senderName ")
            hasJoin = true
            hasWhere = true
            parameters.addValue("senderName", sender.get())
        }

        if (dateFrom.isPresent) {
            sqlQuery.append(" AND DOCUMENTS.created >= :dateFrom ")
            hasWhere = true
            parameters.addValue("dateFrom", dateFrom.get())
        }

        if (dateUntil.isPresent) {
            sqlQuery.append(" AND DOCUMENTS.created <= :dateUntil ")
            hasWhere = true
            parameters.addValue("dateUntil", dateUntil.get())
        }

        sqlQuery.append(" %ORDERBY%")

        var sql = sqlQuery.toString()
        sql = if (hasWhere) {
            sql.replace("%WHERE%", " WHERE 1 = 1 ")
        } else {
            sql.replace("%WHERE%", "")
        }

        sql = if (hasJoin) {
            sql.replace("%JOIN%", " JOIN ")
        } else {
            sql.replace("%JOIN%", sqlJoin.toString())
        }

        var columnSql = sql.replace("%SELECT%", columns)
        columnSql = columnSql.replace("%ORDERBY%", this.getDocumentOrderBy(*order))

        var countSql = sql.replace("%SELECT%", count)
        countSql = countSql.replace("%ORDERBY%", "")

        // first - count the number of results for the given query
        val numberOfEntries = this.jdbcT.query(countSql, parameters, { rs, _ ->
            rs.getLong(1)
        })

        var queryResult: List<Document> = emptyList()
        // if there are no entries - no need to query again!
        if (numberOfEntries[0] > 0) {
            // perform pagination. this is strictly database specific - in this case mysql/mariadb
            // if other databases should be used, this logic need to be changed
            columnSql += if (limit.isPresent) {
                " LIMIT ${limit.get()}"
            } else {
                // it is not possible that there is a "standalone" OFFSET statement - this will result
                // in a SQL error. If the limit value is missing, use the total number of entries as
                // our limit
                " LIMIT ${numberOfEntries[0]}"
            }

            if (skip.isPresent) {
                columnSql += " OFFSET ${skip.get()}"
            }

            // second - perform the query to retrieve the result
            queryResult = this.jdbcT.query(columnSql, parameters, { rs, _ ->
                Document(id = rs.getString("id"),
                        title = rs.getString("title"),
                        fileName = rs.getString("filename"),
                        alternativeId = rs.getString("alternativeid"),
                        previewLink = rs.getString("previewlink"),
                        amount = rs.getDouble("amount"),
                        created = null, //rs.getTimestamp("created"),
                        modified = null, //rs.getTimestamp("modified"),
                        tags = rs.getString("taglist")?.split(";") ?: emptyList(),
                        senders = rs.getString("senderlist")?.split(";") ?: emptyList())
            })
        }

        return PagedDocuments(documents = queryResult, totalEntries = numberOfEntries[0])
    }

    override fun save(document: Document): Document {
        if (StringUtils.isEmpty(document.id)) {
            throw MydmsException("The document-ID has to be assigned to save the element!")
        }

        val item = this.findById(document.id)
        var operationCount: Int

        if (item.isPresent) {
            // update
            operationCount = this.jdbcT.update("UPDATE DOCUMENTS SET title=:title,filename=:filename,alternativeid=" +
                    ":alternativeid,previewlink=:previewlink,amount=:amount,modified=:modified,taglist=:taglist," +
                    "senderlist=:senderlist WHERE id=:id",
                    MapSqlParameterSource()
                            .addValue("title", document.title)
                            .addValue("filename", document.fileName)
                            .addValue("alternativeid", document.alternativeId)
                            .addValue("previewlink", document.previewLink)
                            .addValue("amount", document.amount)
                            .addValue("modified", document.modified)
                            .addValue("taglist", document.tags.joinToString(";"))
                            .addValue("senderlist", document.senders.joinToString(";"))
                            .addValue("id", document.id))
        } else {
            // insert
            operationCount = this.jdbcT.update("INSERT INTO DOCUMENTS (id,title,filename,alternativeid,previewlink," +
                    "amount,created,modified,taglist,senderlist) VALUES(:id,:title,:filename,:alternativeid,:previewlink," +
                    ":amount,:created,:modified,:taglist,:senderlist)",
                    MapSqlParameterSource()
                            .addValue("id", document.id)
                            .addValue("title", document.title)
                            .addValue("filename", document.fileName)
                            .addValue("alternativeid", document.alternativeId)
                            .addValue("previewlink", document.previewLink)
                            .addValue("amount", document.amount)
                            .addValue("created", document.created)
                            .addValue("modified", document.modified)
                            .addValue("taglist", document.tags.joinToString(";"))
                            .addValue("senderlist", document.senders.joinToString(";"))
            )
        }

        if (operationCount == 1) {
            val doc = this.findById(document.id)
            if (doc.isPresent) {
                return doc.get()
            }
        }
        throw MydmsException("Could not retrieve document item!")
    }

    override fun delete(document: Document): Boolean {
        if (StringUtils.isEmpty(document.id)) {
            throw MydmsException("The document-ID has to be assigned to delete the element!")
        }

        val item = this.findById(document.id)
        if (item.isPresent) {
            val operationCount = this.jdbcT.update("DELETE FROM DOCUMENTS WHERE id = :id",
                    MapSqlParameterSource().addValue("id", document.id))

            LOG.debug("Operationresult is $operationCount")

            if (operationCount != 1) {
                throw MydmsException("Could not delete the document. Operationresult is: $operationCount")
            }

            return true
        }

        LOG.warn("The document with id ${document.id} is not available and cannot be deleted.")

        return false
    }

    override fun findAllItems(vararg order: OrderBy): List<Document> {

        val sqlQuery = "SELECT id,title,filename,alternativeid,previewlink," +
                "amount,created,modified,taglist,senderlist FROM DOCUMENTS"

        val sqlOrderBy = this.getDocumentOrderBy(*order)
        val sqlQueryOrderBy = "$sqlQuery $sqlOrderBy"

        return this.jdbcT.query(sqlQueryOrderBy, { rs, _ ->
            Document(id = rs.getString("id"),
                    title = rs.getString("title"),
                    fileName = rs.getString("filename"),
                    alternativeId = rs.getString("alternativeid"),
                    previewLink = rs.getString("previewlink"),
                    amount = rs.getDouble("amount"),
                    created = null, //rs.getTimestamp("created"),
                    modified = null, //rs.getTimestamp("modified"),
                    tags = rs.getString("taglist")?.split(";") ?: emptyList(),
                    senders = rs.getString("senderlist")?.split(";") ?: emptyList())
        })
    }

    private fun getDocumentOrderBy(vararg order: OrderBy): String {
        if (order.isEmpty())
            return ""

        val orderFields = StringBuffer(" ORDER BY ")
        for ((index, orderBy) in order.withIndex()) {
            if (index > 0)
                orderFields.append(",")

            if (orderBy.sort === SortOrder.Ascending) {
                orderFields.append(" DOCUMENTS.${orderBy.field} ASC")
            } else {
                orderFields.append(" DOCUMENTS.${orderBy.field} DESC")
            }
        }
        return orderFields.toString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JdbcDocumentStore::class.java)
    }
}

