package net.binggl.mydms.features.documents.data

import net.binggl.mydms.features.documents.models.Document
import net.binggl.mydms.features.documents.models.OrderBy
import net.binggl.mydms.features.documents.models.PagedDocuments
import java.util.*

interface DocumentStore {

    /**
     * retrieve a document by it's id
     * @param id the unique id of the document
     * @return the document object as an Optional
     */
    fun findById(id: String): Optional<Document>

    /**
     * search for documents by a number of arguments
     * @param title search by title
     * @param tag search by tagname
     * @param sender search by sendername
     * @param dateFrom search by starting date
     * @param dateUntil search by end date
     * @param limit limit the number of results returned
     * @param skip use an offset
     * @param order sort the result by
     * @return object containing documents used for paging view
     */
    fun searchDocuments(tile: Optional<String>, tag: Optional<String>, sender: Optional<String>,
                        dateFrom: Optional<Date>, dateUntil: Optional<Date>, limit: Optional<Int>, skip: Optional<Int>,
                        vararg order: OrderBy): PagedDocuments

    /**
     * save a document - either insert or update
     * @param document the document object
     * @return the saved document object
     */
    fun save(document: Document): Document

    /**
     * delete a document from the store
     * @param document a valid document object
     * @return result of the operation
     */
    fun delete(document: Document): Boolean

    /**
     * retrieve all documents from the store
     * @param order order by arguments
     * @return list of documents
     */
    fun findAllItems(vararg order: OrderBy): List<Document>

}
