package net.binggl.mydms.features.filestore

import net.binggl.mydms.features.filestore.model.FileItem
import java.util.*

interface FileService {
    /**
     * save the given file in the backend
     * @param file
     * @return
     */
    fun saveFile(file: FileItem): Boolean

    /**
     * retrieve a file from the service
     * @param filePath the path to the file in the store
     * @return Optional object of a file
     */
    fun getFile(filePath: String): Optional<FileItem>

}