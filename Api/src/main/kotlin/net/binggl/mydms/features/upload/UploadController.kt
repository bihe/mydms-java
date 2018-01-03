package net.binggl.mydms.features.upload

import net.binggl.mydms.features.upload.models.UploadItem
import net.binggl.mydms.features.upload.models.UploadResult
import net.binggl.mydms.infrastructure.config.UploadConfig
import net.binggl.mydms.infrastructure.error.MydmsException
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.ActionResult
import net.binggl.mydms.shared.models.Role
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
class UploadController(@Autowired private val store: UploadStore,
                       @Autowired private val config: UploadConfig): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @PostMapping
    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadResult {
        LOG.debug("Got file: $file")

        if (file.size > config.maxUploadSize) {
            throw MydmsException("The file exceeds the maximum upload size of ${config.maxUploadSize}")
        }

        if (!config.allowedFileTypes.any { it == file.contentType }) {
            throw MydmsException("The supplied mime-type is not allowed: ${file.contentType}")
        }

        val id = UUID.randomUUID().toString()
        val uploadQueueItem = UploadItem(id, file.originalFilename, file.contentType)

        LOG.debug("Will save the given file ${file.originalFilename} using the created token $id")

        var fileExtension = FilenameUtils.getExtension(file.originalFilename)
        val outputPath: java.nio.file.Path = FileSystems.getDefault().getPath(config.uploadPath,"$id.$fileExtension")
        Files.copy(file.inputStream, outputPath)

        this.store.save(uploadQueueItem)
        return UploadResult(token = uploadQueueItem.id,
                message = "File ${file.originalFilename} was uploaded and stored using token $id",
                result = ActionResult.Created)
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(UploadController::class.java)
    }
}