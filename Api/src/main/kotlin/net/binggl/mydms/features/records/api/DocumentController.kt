package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.features.records.repository.DocumentRepository
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/documents")
class DocumentController(@Autowired private val repository: DocumentRepository): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping(produces = ["application/json"])
    @Transactional(readOnly = true) // transaction is needed for the related/lazily loaded collections
    fun getAll(): List<Document> {
        return this.repository.searchDocuments(
                title = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                skip = Optional.of(0),
                limit = Optional.empty(),
                order = *arrayOf(
                        OrderBy("created", SortOrder.Descending),
                        OrderBy("title", SortOrder.Ascending)
                )
        ).documents
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping(value = "/{id}", produces = ["application/json"])
    @Transactional(readOnly = true)
    fun getDocumentById(@PathVariable id: String): ResponseEntity<*> {
        val entity = this.repository.findById(id)
        if (entity.isPresent) {
            val document = entity.get()
            return ResponseEntity.ok(
                Document(
                        id = document.id,
                        title = document.title,
                        created = document.created,
                        alternativeId = document.alternativeId,
                        tags = document.tags.map { it.name },
                        modified = document.modified,
                        amount = document.amount,
                        uploadFileToken = null,
                        senders = document.senders.map { it.name },
                        previewLink = document.previewLink,
                        fileName = document.fileName)
            )
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Could not find the document with id '$id'!")
    }
}