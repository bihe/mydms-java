package net.binggl.mydms.features.gdrive

import net.binggl.commons.crypto.HashHelper
import net.binggl.mydms.features.gdrive.client.GDriveClient
import net.binggl.mydms.features.gdrive.models.GDriveCredential
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore
import net.binggl.mydms.infrastructure.exceptions.MydmsException
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.ActionResult
import net.binggl.mydms.shared.models.Role
import net.binggl.mydms.shared.models.SimpleResult
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/gdrive")
class GDriveController(@Autowired private val client: GDriveClient,
                       @Autowired private val credentialStore: GDriveCredentialStore,
                       @Value("\${google.parentDrivePath}") private val parentDrivePath: String,
                       @Value("\${google.successUrl}") private val successUrl: String): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping()
    fun isLinked(): Boolean {
        return this.credentialStore.isCredentialAvailable("")
    }

    @ApiSecured(requiredRole = Role.Admin)
    @GetMapping("/link")
    fun link(request: HttpServletRequest): ResponseEntity<Any> {
        val correlationToken = HashHelper.getSHA(USER_TOKEN, Date().toString())
        val session = request.getSession(true) // ensure a new session
        session.setAttribute(SESSION_CORRELATION_TOKEN, correlationToken)
        val redirect = this.client.getRedirectUrl(correlationToken)

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header("location", redirect)
                .build()
    }

    @ApiSecured(requiredRole = Role.Admin)
    @GetMapping("/oauth2callback")
    fun callback(@RequestParam("code") authorizationCode: String,
                 @RequestParam("state") correlationToken: String,
                 request: HttpServletRequest): ResponseEntity<Any> {
        val session = request.getSession(false)
        if (session.isNew) {
            throw MydmsException("Could not reuse session for correlation!")
        }

        val sessionCorrelationToken = session.getAttribute(SESSION_CORRELATION_TOKEN)
        if (correlationToken != sessionCorrelationToken) {
            throw MydmsException("The correlation token does not match. Cannot link account!")
        }

        // cleanup
        session.removeAttribute(SESSION_CORRELATION_TOKEN)
        val credentials = this.client.getCredentials(authorizationCode, USER_TOKEN)
        LOG.debug("Got credentials: $credentials")
        this.credentialStore.save(USER_TOKEN, credentials)

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .header("location", successUrl)
                .build()
    }

    @ApiSecured(requiredRole = Role.Admin)
    @DeleteMapping()
    fun unlinkAccount(): SimpleResult {
        this.credentialStore.clearCredentials(USER_TOKEN)
        return SimpleResult(message = "Stored credentials where deleted.", result = ActionResult.Deleted)
    }

    @ApiSecured(requiredRole = Role.User)
    fun getFile(@RequestParam("path") path: String): ResponseEntity<Any> {
        if (!this.credentialStore.isCredentialAvailable(USER_TOKEN)) {
            throw MydmsException("Could not get files: Account is not linked!")
        }

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

        val filePayload = this.client.getFile(this.credentials, decodedPath, parentDrivePath)
        if (filePayload.isPresent) {
            return ResponseEntity
                    .ok()
                    .contentLength(filePayload.get().payload.size.toLong())
                    .contentType(MediaType.parseMediaType(filePayload.get().mimeType))
                    .header("content-disposition", "attachment; filename = " + filePayload.get().getName())
                    .body(filePayload.get().payload)
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The given path is not available.")
    }

    private val credentials: GDriveCredential
            get() {
                val cred = this.credentialStore.load(USER_TOKEN)
                LOG.debug("Got credential: $cred")
                return cred
            }


    companion object {
        private const val USER_TOKEN = "mydms.user.gdrive"
        private const val SESSION_CORRELATION_TOKEN = "session.mydms.correlation"
        private val LOG = LoggerFactory.getLogger(GDriveController::class.java)
    }
}