package net.binggl.mydms.features.upload.models

import net.binggl.mydms.shared.models.ActionResult

data class UploadResult(val token: String, val message: String, val result: ActionResult)