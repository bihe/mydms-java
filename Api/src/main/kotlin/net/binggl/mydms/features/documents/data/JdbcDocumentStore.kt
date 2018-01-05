package net.binggl.mydms.features.documents.data

import net.binggl.mydms.features.documents.models.Document
import net.binggl.mydms.features.documents.models.OrderBy
import net.binggl.mydms.features.documents.models.PagedDocuments
import net.binggl.mydms.features.documents.models.SortOrder
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
                    created = rs.getTimestamp("created"),
                    modified = rs.getTimestamp("modified"),
                    tags = rs.getString("taglist")?.split(";") ?: emptyList(),
                    senders = rs.getString("senderlist")?.split(";") ?: emptyList())
        })

        if (result.isEmpty()) {
            return Optional.empty()
        }
        return Optional.of(result[0])
    }

    override fun searchDocuments(tile: Optional<String>, tag: Optional<String>, sender: Optional<String>, dateFrom: Optional<Date>, dateUntil: Optional<Date>, limit: Optional<Int>, skip: Optional<Int>, vararg order: OrderBy): PagedDocuments {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                    created = rs.getTimestamp("created"),
                    modified = rs.getTimestamp("modified"),
                    tags = rs.getString("taglist")?.split(";") ?: emptyList(),
                    senders = rs.getString("senderlist")?.split(";") ?: emptyList())
        })
    }

    private fun getDocumentOrderBy(vararg order: OrderBy): String {
        if (order == null || order.size == 0)
            return ""

        val orderFields = StringBuffer(" ORDER BY ")
        for ((index, order) in order.withIndex()) {
            if (index > 0)
                orderFields.append(",")

            if (order.sort === SortOrder.Ascending) {
                orderFields.append(" DOCUMENTS.${order.field} ASC")
            } else {
                orderFields.append(" DOCUMENTS.${order.field} DESC")
            }
        }
        return orderFields.toString()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JdbcDocumentStore::class.java)
    }
}

