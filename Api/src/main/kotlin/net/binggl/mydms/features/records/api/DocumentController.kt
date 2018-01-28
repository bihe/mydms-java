package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.features.records.repository.DocumentRepository
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.ActionResult
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.models.SimpleResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@RestController
@RequestMapping("/api/v1/documents")
class DocumentController(
        @Value("\${application.defaultLimit}") private val defaultLimitValue: Int,
        @Autowired private val repository: DocumentRepository): BaseResource() {

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
    @GetMapping(value = ["/{id}"], produces = ["application/json"])
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.error("Could not find the document with id '$id'!"))
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping(value = ["/search"], produces = ["application/json"])
    @Transactional(readOnly = true)
    fun searchDocuments(@RequestParam("title") title: Optional<String>,
                        @RequestParam("tag") byTag: Optional<String>,
                        @RequestParam("sender") bySender: Optional<String>,
                        @RequestParam("from") fromDateString: Optional<String>,
                        @RequestParam("to") untilDateString: Optional<String>,
                        @RequestParam("limit") limit: Optional<Int>,
                        @RequestParam("skip") skip: Optional<Int>): PagedDocuments {

        // set defaults
        val limitResults = if (limit.isPresent) limit.get() else defaultLimitValue
        val fromDate = if (fromDateString.isPresent) {
            this.parseDateTime(fromDateString.get())
        } else {
            Optional.empty()
        }
        val untilDate = if (untilDateString.isPresent) {
            this.parseDateTime(untilDateString.get())
        } else {
            Optional.empty()
        }

        return this.repository.searchDocuments(title, byTag, bySender, fromDate, untilDate,
                Optional.of(limitResults), skip)

    }

    @ApiSecured(requiredRole = Role.User)
    @DeleteMapping(value = ["/{id}"], produces = ["application/json"])
    @Transactional()
    fun deleteDocument(@PathVariable id: String): ResponseEntity<*> {
        LOG.debug("Will attempt to delete document with id '$id'.")
        val document = this.repository.findById(id)

        return if (document.isPresent) {
            LOG.debug("Got document with id: $id - delete it!")
            this.repository.deleteById(id)
            LOG.info("Document with id '$id' was deleted.")
            ResponseEntity.ok(SimpleResult(
                    message = "Document with id '$id' was deleted.",
                    result = ActionResult.Deleted)
            )
        } else {
            LOG.warn("Cannot delete unknown document with id '$id'")
            ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.error("Could not delete document with id '$id'!"))
        }
    }

    private fun parseDateTime(input: String): Optional<LocalDateTime> {
        return try {
            Optional.of(LocalDateTime.parse(input, fmt))
        } catch(ex: DateTimeParseException) {
            LOG.warn("Supplied date format is invalid: $input - parse error: ${ex.message}")
            Optional.empty()
        }
    }

    private fun error(message: String): SimpleResult {
        return SimpleResult(message = message, result = ActionResult.Error)
    }

    companion object {
        private val fmt = DateTimeFormatter.ISO_DATE_TIME
        private val LOG = LoggerFactory.getLogger(DocumentController::class.java)
    }
}