package net.binggl.mydms.features.records.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.binggl.mydms.shared.util.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty

data class Document(val id: String, @NotEmpty val title: String, @NotEmpty val fileName: String,
                    val alternativeId: String, val previewLink: String, val amount: Double,
                    @JsonSerialize(using = JsonDateSerializer::class) val created: LocalDateTime?,
                    @JsonSerialize(using = JsonDateSerializer::class) val modified: LocalDateTime?,
                    val tags: List<String>,
                    val senders: List<String>,
                    val uploadFileToken: String?) {

    constructor(id: String, @NotEmpty title: String, @NotEmpty fileName: String,
                alternativeId: String, previewLink: String, amount: Double,
                @JsonSerialize(using = JsonDateSerializer::class) created: LocalDateTime?,
                @JsonSerialize(using = JsonDateSerializer::class) modified: LocalDateTime?,
                tags: List<String>,
                senders: List<String>) : this(id, title, fileName, alternativeId, previewLink, amount, created,
            modified, tags, senders, null)

    constructor(id: String, @NotEmpty title: String, @NotEmpty fileName: String,
                alternativeId: String, previewLink: String, amount: Double,
                @JsonSerialize(using = JsonDateSerializer::class) created: LocalDateTime?,
                @JsonSerialize(using = JsonDateSerializer::class) modified: LocalDateTime?) : this(id, title, fileName,
            alternativeId, previewLink, amount, created, modified, emptyList(), emptyList(), null)
}
