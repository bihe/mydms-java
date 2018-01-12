package net.binggl.mydms.features.records.entities

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import net.binggl.mydms.shared.util.JsonDateSerializer
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
@Table(name = "DOCUMENTS")
internal data class Document(
        @Id
        @Column(name = "id")
        val id: String,

        @Column(name = "title", nullable = false)
        @NotEmpty
        val title: String,

        @Column(name = "filename", nullable = false)
        @NotEmpty
        val fileName: String,

        @Column(name = "alternativeid", unique = true)
        val alternativeId: String,

        @Column(name = "previewlink")
        val previewLink: String,

        @Column(name = "amount")
        val amount: Double,

        @Column(name = "created")
        @JsonSerialize(using = JsonDateSerializer::class)
        val created: Date,

        @Column(name = "modified")
        @JsonSerialize(using = JsonDateSerializer::class)
        val modified: Date,

        @Column(name = "taglist")
        val tagList: String,

        @Column(name = "senderlist")
        val senderList: String,

        // associations
        // example of ManyToMany credit to: https://hellokoding.com/jpa-many-to-many-relationship-mapping-example-with-spring-boot-maven-and-mysql/

        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "DOCUMENTS_TO_TAGS",
                joinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "document_id", referencedColumnName = "id")])
        val tags: List<Tag> = emptyList(),

        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "DOCUMENTS_TO_SENDERS",
        joinColumns = [JoinColumn(name = "sender_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "document_id", referencedColumnName = "id")])
        val senders: List<Sender> = emptyList()
)