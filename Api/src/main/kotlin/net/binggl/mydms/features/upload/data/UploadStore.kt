package net.binggl.mydms.features.upload.data

import net.binggl.mydms.features.upload.models.UploadItem
import java.util.*

interface UploadStore {

    /**
     * return all upload items available
     * @return list of UploadItems
     */
    fun findAll(): List<UploadItem>

    /**
     * find a specific upload item
     * @param token the id of the item in the store
     * @return Optional<UploadItem> element - empty if not found
     */
    fun findById(token: String): Optional<UploadItem>

    /**
     * save an item - either insert or update
     * @param uploadItem the item to save
     * @return the item after the save operation
     */
    fun save(uploadItem: UploadItem): UploadItem

    /**
     * delete the given item
     * the opeation fails silently on error - failures are logged
     * @param uploadItem valid item to delete
     */
    fun delete(uploadItem: UploadItem)
}