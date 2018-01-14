package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import javax.persistence.EntityManager

internal class DocumentSearchImpl : DocumentSearch {

    @Autowired
    private lateinit var entityManager: EntityManager

    override fun searchDocuments(tile: Optional<String>, tag: Optional<String>,
                                 sender: Optional<String>, dateFrom: Optional<Date>,
                                 dateUntil: Optional<Date>, limit: Optional<Int>,
                                 skip: Optional<Int>, vararg order: OrderBy): PagedDocuments {




        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}