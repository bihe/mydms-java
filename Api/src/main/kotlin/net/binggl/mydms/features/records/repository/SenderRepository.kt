package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entities.Sender
import org.springframework.data.repository.CrudRepository
import java.util.stream.Stream

internal interface SenderRepository : CrudRepository<Sender, Long> {

    /**
     * search for Tags with the given name
     * @param name the name of the tags
     *
     */
    fun findByNameContainingIgnoreCase(name: String): List<Sender>
}