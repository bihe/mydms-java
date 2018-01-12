package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entities.Tag
import org.springframework.data.repository.CrudRepository

internal interface TagRepository : CrudRepository<Tag, Long> {

    /**
     * search for Tags with the given name
     * @param name the name of the tags
     *
     */
    fun findByNameContainingIgnoreCase(name: String): List<Tag>
}