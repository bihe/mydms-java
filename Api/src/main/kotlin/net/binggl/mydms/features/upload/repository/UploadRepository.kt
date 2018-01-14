package net.binggl.mydms.features.upload.repository

import net.binggl.mydms.features.upload.entity.UploadEntity
import org.springframework.data.repository.CrudRepository

interface UploadRepository : CrudRepository<UploadEntity, String> {

}