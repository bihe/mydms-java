package net.binggl.mydms.features.gdrive

import net.binggl.mydms.features.gdrive.client.GDriveClient
import net.binggl.mydms.features.gdrive.models.GDriveCredential
import net.binggl.mydms.features.gdrive.store.GDriveCredentialStore
import net.binggl.mydms.infrastructure.error.MydmsException
import net.binggl.mydms.shared.files.FileItem
import net.binggl.mydms.shared.files.FileService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.util.*

class GDriveFileService(@Autowired private val client: GDriveClient,
                        @Autowired private val credentialStore: GDriveCredentialStore,
                        @Value("\${google.parentDrivePath}") private val parentDrivePath: String): FileService {
    override fun saveFile(file: FileItem): Boolean {
        if (!this.credentialStore.isCredentialAvailable(Contants.USER_TOKEN)) {
            throw MydmsException("Could not save file: Account is not linked!")
        }

        val cred = this.credentials

        var folder = this.client.getFolder(cred, file.folderName, parentDrivePath)
        if (!folder.isPresent) {
            LOG.debug("Create new foldername ${file.folderName}")
            val newFolder = this.client.createFolder(cred, file.folderName, parentDrivePath)
            LOG.debug("Created new folder-item $newFolder")
            folder = Optional.of(newFolder)
        }

        val item = this.client.saveItem(cred,
                file.fileName,
                file.mimeType,
                file.payload.toByteArray(),
                folder.get().id)

        LOG.debug("Created new item $item")

        return true
    }


    private val credentials: GDriveCredential
        get() {
            return this.credentialStore.load(Contants.USER_TOKEN)
        }

    companion object {
        private val LOG = LoggerFactory.getLogger(GDriveFileService::class.java)
    }
}