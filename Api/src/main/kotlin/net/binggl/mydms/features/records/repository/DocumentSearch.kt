package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import java.time.LocalDateTime
import java.util.*

interface DocumentSearch {

    /**
     * search for records by a number of arguments
     * @param title search by title
     * @param tag search by tagname
     * @param sender search by sendername
     * @param dateFrom search by starting date
     * @param dateUntil search by end date
     * @param limit limit the number of results returned
     * @param skip use an offset
     * @param order sort the result by
     * @return object containing records used for paging view
     */
    fun searchDocuments(title: Optional<String>,
                        tag: Optional<String>,
                        sender: Optional<String>,
                        dateFrom: Optional<LocalDateTime>,
                        dateUntil: Optional<LocalDateTime>,
                        limit: Optional<Int>,
                        skip: Optional<Int>,
                        vararg order: OrderBy): PagedDocuments
}
