package net.binggl.mydms.features.upload.models

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.binggl.mydms.shared.util.JsonDateSerializer
import java.util.*

data class UploadItem(val id: String, val fileName: String, val mimeType: String, @JsonSerialize(using = JsonDateSerializer::class) val created: Date?) {
    constructor(id: String, fileName: String, mimeType: String): this(id, fileName, mimeType, null)
}