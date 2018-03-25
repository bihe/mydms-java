package net.binggl.mydms.features.upload.api

import net.binggl.mydms.features.upload.UploadConfig
import net.binggl.mydms.features.upload.entity.UploadEntity
import net.binggl.mydms.features.upload.model.UploadResult
import net.binggl.mydms.features.upload.repository.UploadRepository
import net.binggl.mydms.infrastructure.error.MydmsException
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.ActionResult
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.*

@RestController
@RequestMapping("/api/v1/upload")
class UploadController(@Autowired private val repository: UploadRepository,
                       @Autowired private val config: UploadConfig): BaseResource() {

    @PostMapping("/file")
    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadResult {
        LOG.debug("Got file: $file")

        if (file.size > config.maxUploadSize) {
            throw MydmsException("The file exceeds the maximum upload size of ${config.maxUploadSize}")
        }

        if (!config.allowedFileTypes.any { it == file.contentType }) {
            throw MydmsException("The supplied mime-type is not allowed: ${file.contentType}")
        }

        val id = UUID.randomUUID().toString()
        val uploadQueueItem = UploadEntity(id, file.originalFilename, file.contentType)

        LOG.debug("Will save the given file ${file.originalFilename} using the created token $id")

        var fileExtension = FilenameUtils.getExtension(file.originalFilename)
        val outputPath: java.nio.file.Path = FileSystems.getDefault().getPath(config.uploadPath,"$id.$fileExtension")
        Files.copy(file.inputStream, outputPath)

        this.repository.save(uploadQueueItem)
        return UploadResult(token = uploadQueueItem.id,
                message = "File ${file.originalFilename} was uploaded and stored using token $id",
                result = ActionResult.Created)
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(UploadController::class.java)
    }
}