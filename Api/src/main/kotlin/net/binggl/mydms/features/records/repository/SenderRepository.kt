package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.Sender
import org.springframework.data.repository.CrudRepository

internal interface SenderRepository : CrudRepository<Sender, Long> {

    /**
     * search for Tags with the given name
     * @param name the name of the tags
     *
     */
    fun findByNameContainingIgnoreCase(name: String): List<Sender>
}