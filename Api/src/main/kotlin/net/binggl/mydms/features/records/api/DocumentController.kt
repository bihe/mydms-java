package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.filestore.FileService
import net.binggl.mydms.features.filestore.model.FileItem
import net.binggl.mydms.features.records.entity.DocumentEntity
import net.binggl.mydms.features.records.entity.SenderEntity
import net.binggl.mydms.features.records.entity.TagEntity
import net.binggl.mydms.features.records.model.Document
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.PagedDocuments
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.features.records.repository.DocumentRepository
import net.binggl.mydms.features.records.repository.SenderRepository
import net.binggl.mydms.features.records.repository.TagRepository
import net.binggl.mydms.features.upload.UploadConfig
import net.binggl.mydms.features.upload.repository.UploadRepository
import net.binggl.mydms.infrastructure.error.MydmsException
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.ActionResult
import net.binggl.mydms.shared.models.SimpleResult
import net.binggl.mydms.shared.util.toBase64
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.FileSystems
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull


@RestController
@RequestMapping("/api/v1/documents")
class DocumentController(
        @Value("\${application.defaultLimit}") private val defaultLimitValue: Int,
        @Autowired private val uploadConfig: UploadConfig,
        @Autowired private val repository: DocumentRepository,
        @Autowired private val tagRepository: TagRepository,
        @Autowired private val senderRepository: SenderRepository,
        @Autowired private val uploadRepository: UploadRepository,
        @Autowired private val fileService: FileService): BaseResource() {

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
                        previewLink = document.fileName.toBase64(),
                        fileName = document.fileName)
            )
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.error("Could not find the document with id '$id'!"))
    }

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

        val orderByDateDesc = OrderBy("created", SortOrder.Descending)
        val orderByName = OrderBy("title", SortOrder.Ascending)

        return this.repository.searchDocuments(title, byTag, bySender, fromDate, untilDate,
                Optional.of(limitResults), skip, orderByDateDesc, orderByName)

    }

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

    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    @Transactional()
    fun saveDocument(@RequestBody @NotNull @Valid documentPayload: Document): ResponseEntity<SimpleResult> {

        val tagList = this.processTags(documentPayload.tags)
        val senderList = this.processSenders(documentPayload.senders)
        val fileName = this.processUploadFile(documentPayload.uploadFileToken, documentPayload.fileName)
        var newDoc = true

        val document = if (StringUtils.isEmpty(documentPayload.id)) {
            LOG.debug("No id supplied - create a new document.")
            this.newDocumentInstance(documentPayload, fileName, tagList, senderList)
        } else {
            LOG.debug("Id available - lookup document with id '${documentPayload.id}'")
            val doc = this.repository.findById(documentPayload.id)
            if (doc.isPresent) {
                LOG.debug("Got document with id '${documentPayload.id}'")
                newDoc = false
                doc.get().copy(title = documentPayload.title,
                        fileName = fileName,
                        previewLink = fileName.toBase64(),
                        amount = documentPayload.amount,
                        modified = LocalDateTime.now(),
                        tags = tagList,
                        senders = senderList,
                        senderList = senderList.joinToString(";") { it.name },
                        tagList = tagList.joinToString(";") { it.name })
            } else {
                LOG.debug("No document for id '${documentPayload.id}' - create a new document.")
                this.newDocumentInstance(documentPayload, fileName, tagList, senderList)
            }
        }

        val saveDocument = this.repository.save(document)
        val result = if (newDoc) {
            SimpleResult(message = "Created new document '${saveDocument.title}' (${saveDocument.id})",
                    result = ActionResult.Created)
        } else {
            SimpleResult(message = "Updated existing document '${saveDocument.title}' (${saveDocument.id})",
                    result = ActionResult.Updated)
        }

        return ResponseEntity.ok(result)

    }



    private fun processUploadFile(uploadToken: String?, fileName: String): String {
        if (uploadToken == null || uploadToken == "-")
            return fileName

        val result = this.uploadRepository.findById(uploadToken)
        if (!result.isPresent) {
            throw MydmsException("Could not get an uploaded document with the given correlation token '$uploadToken'!")
        }

        LOG.debug("Read upload-item from store - id: '${result.get().id}'")

        val folderName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
        val uploadFile = this.getFile(fileName, this.uploadConfig.uploadPath, uploadToken)
        val uploadFileContents = FileUtils.readFileToByteArray(uploadFile)

        LOG.debug("Got upload file '${uploadFile.name}' with payload size '${uploadFileContents.size}'!")

        val fileItem = FileItem(fileName = fileName, mimeType = result.get().mimeType,
                payload = uploadFileContents.toTypedArray(), folderName = folderName)

        if (!this.fileService.saveFile(fileItem)) {
            throw MydmsException("Could not save file in backend store '${fileItem.fileName}'")
        }

        val filePath = "/$folderName/${fileItem.fileName}"

        if (!uploadFile.delete()) {
            LOG.warn("Could not delete upload file on filesystem! $uploadToken")
        }

        this.uploadRepository.deleteById(result.get().id)
        return filePath
    }

    private fun getFile(fileName: String, uploadPath: String, uploadToken: String): File {
        val fileExtension = FilenameUtils.getExtension(fileName)
        val outputPath = FileSystems.getDefault().getPath(uploadPath,
                String.format("%s.%s", uploadToken, fileExtension))
        return outputPath.toFile()
    }

    private fun processTags(tags: List<String>): Set<TagEntity> {
        return if (tags.isNotEmpty()) {
            tags.map {
                val result = this.tagRepository.findByNameContainingIgnoreCase(it)
                if (result.size == 1) {
                    result[0]
                } else {
                    this.tagRepository.save(TagEntity(name = it))
                }
            }.toSet()
        } else {
            emptySet()
        }
    }

    private fun processSenders(senders: List<String>): Set<SenderEntity> {
        return if (senders.isNotEmpty()) {
            senders.map {
                val result = this.senderRepository.findByNameContainingIgnoreCase(it)
                if (result.isNotEmpty()) {
                    result[0]
                } else {
                    this.senderRepository.save(SenderEntity(name = it))
                }
            }.toSet()
        } else {
            emptySet()
        }
    }

    private fun newDocumentInstance(documentPayload: Document,
                                    fileName: String,
                                    tagSet: Set<TagEntity>,
                                    senderSet: Set<SenderEntity>): DocumentEntity {
        return DocumentEntity(id = UUID.randomUUID().toString(),
                title = documentPayload.title,
                alternativeId = RandomStringUtils.random(8, true, true),
                fileName = fileName,
                previewLink = fileName.toBase64(),
                amount = documentPayload.amount,
                created = LocalDateTime.now(),
                senders = senderSet,
                tags = tagSet,
                modified = null,
                senderList = senderSet.joinToString(";") { it.name },
                tagList = tagSet.joinToString(";") { it.name }
        )
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