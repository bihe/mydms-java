package net.binggl.mydms.features.records.models

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.binggl.mydms.shared.util.JsonDateSerializer
import java.util.*
import javax.validation.constraints.NotEmpty

data class Document(val id: String, @NotEmpty val title: String, @NotEmpty val fileName: String,
                    val alternativeId: String, val previewLink: String, val amount: Double,
                    @JsonSerialize(using = JsonDateSerializer::class) val created: Date?,
                    @JsonSerialize(using = JsonDateSerializer::class) val modified: Date?,
                    val tags: List<String>,
                    val senders: List<String>,
                    val uploadFileToken: String?) {

    constructor(id: String, @NotEmpty title: String, @NotEmpty fileName: String,
                alternativeId: String, previewLink: String, amount: Double,
                @JsonSerialize(using = JsonDateSerializer::class) created: Date?,
                @JsonSerialize(using = JsonDateSerializer::class) modified: Date?,
                tags: List<String>,
                senders: List<String>) : this(id, title, fileName, alternativeId, previewLink, amount, created,
            modified, tags, senders, null)

    constructor(id: String, @NotEmpty title: String, @NotEmpty fileName: String,
                alternativeId: String, previewLink: String, amount: Double,
                @JsonSerialize(using = JsonDateSerializer::class) created: Date?,
                @JsonSerialize(using = JsonDateSerializer::class) modified: Date?) : this(id, title, fileName,
            alternativeId, previewLink, amount, created, modified, emptyList(), emptyList(), null)
}
