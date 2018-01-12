package net.binggl.mydms.features.records.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "TAGS")
internal data class Tag(

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @JsonIgnore
        @ManyToMany(mappedBy = "tags")
        val documents: List<Document> = emptyList()
) {
    constructor(name: String) : this(null, name, emptyList())
}