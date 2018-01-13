package net.binggl.mydms.features.upload.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "UPLOADS")
data class Upload(

        @Id
        @Column(name = "id")
        val id: String,

        @Column(name = "filename", nullable = false)
        val fileName: String,

        @Column(name = "mimetype", nullable = false)
        val mimeType: String,

        @Column(name = "created")
        val created: LocalDateTime) {
    constructor(id: String, fileName: String, mimeType: String) : this(id, fileName, mimeType, LocalDateTime.now())
}
