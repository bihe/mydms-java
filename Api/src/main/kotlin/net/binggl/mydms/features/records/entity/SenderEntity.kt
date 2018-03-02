package net.binggl.mydms.features.records.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "SENDERS")
data class SenderEntity(

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @JsonIgnore
        @ManyToMany(mappedBy = "senders")
        val documents: List<DocumentEntity> = emptyList()
) {
    constructor(name: String) : this(null, name, emptyList())
}