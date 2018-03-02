package net.binggl.mydms.features.records.entity

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
@Table(name = "DOCUMENTS")
data class DocumentEntity(
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
        val previewLink: String?,

        @Column(name = "amount")
        val amount: Double,

        @Column(name = "created")
        val created: LocalDateTime,

        @Column(name = "modified")
        val modified: LocalDateTime?,

        @Column(name = "taglist")
        val tagList: String,

        @Column(name = "senderlist")
        val senderList: String,

        // associations
        // example of ManyToMany credit to: https://hellokoding.com/jpa-many-to-many-relationship-mapping-example-with-spring-boot-maven-and-mysql/

        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(name = "DOCUMENTS_TO_TAGS",
                joinColumns = [JoinColumn(name = "document_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "tag_id", referencedColumnName = "id")])
        val tags: Set<TagEntity> = emptySet(),

        @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinTable(name = "DOCUMENTS_TO_SENDERS",
                joinColumns = [JoinColumn(name = "document_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "sender_id", referencedColumnName = "id")])
        val senders: Set<SenderEntity> = emptySet()
)