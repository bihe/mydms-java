package net.binggl.mydms.features.filestore.api

import net.binggl.mydms.features.filestore.FileService
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/v1/file")
class FileController(@Autowired private val fileService: FileService): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping
    fun getFile(@RequestParam("path") path: String): ResponseEntity<Any> {

        LOG.debug("Supplied path: $path")

        var decodedPath: String
        try {
            val decodedPathPayload = Base64.decodeBase64(path)
            decodedPath = String(decodedPathPayload, StandardCharsets.UTF_8)
            decodedPath = URLDecoder.decode(decodedPath, "UTF-8")
            LOG.debug("Decoded path: $decodedPath")
        } catch (ex: UnsupportedEncodingException) {
            LOG.warn("Could not parse/decode the supplied path: $path")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid path suppplied!")
        }

        val file = this.fileService.getFile(decodedPath)
        if (file.isPresent) {
            val backendFile = file.get()
            return ResponseEntity
                    .ok()
                    .contentLength(backendFile.payload.size.toLong())
                    .contentType(MediaType.parseMediaType(backendFile.mimeType))
                    .header("content-disposition", "attachment; filename = " + backendFile.fileName)
                    .body(backendFile.payload.toByteArray())
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The given path is not available.")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FileController::class.java)
    }
}
