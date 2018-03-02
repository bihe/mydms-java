package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.TagEntity
import org.springframework.data.repository.CrudRepository

interface TagRepository : CrudRepository<TagEntity, Long> {

    /**
     * search for Tags with the given name
     * @param name the name of the tagEntities
     *
     */
    fun findByNameContainingIgnoreCase(name: String): List<TagEntity>
}