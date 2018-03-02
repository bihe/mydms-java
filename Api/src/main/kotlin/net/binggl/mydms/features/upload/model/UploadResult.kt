package net.binggl.mydms.features.upload.model

import net.binggl.mydms.shared.models.ActionResult

data class UploadResult(val token: String, val message: String, val result: ActionResult)