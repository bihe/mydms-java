package net.binggl.mydms.features.records.repository

import net.binggl.mydms.features.records.entity.SenderEntity
import org.springframework.data.repository.CrudRepository

interface SenderRepository : CrudRepository<SenderEntity, Long> {

    /**
     * search for Tags with the given name
     * @param name the name of the tagEntities
     *
     */
    fun findByNameContainingIgnoreCase(name: String): List<SenderEntity>
}