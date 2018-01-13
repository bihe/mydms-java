package net.binggl.mydms.features.upload.repository

import net.binggl.mydms.features.upload.entity.Upload
import org.springframework.data.repository.CrudRepository

interface UploadRepository : CrudRepository<Upload, String> {

}