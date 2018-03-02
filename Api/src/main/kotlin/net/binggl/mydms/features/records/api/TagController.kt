package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.entity.TagEntity
import net.binggl.mydms.features.records.model.Tag
import net.binggl.mydms.features.records.repository.TagRepository
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tags")
class TagController(@Autowired private val repository: TagRepository): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("")
    fun allTags() : List<Tag> {
        var entities = repository.findAll()?.toList() ?: emptyList<TagEntity>()
        return entities.map {
            Tag(it.id, it.name)
        }
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/search")
    fun searchTags(@RequestParam(value="name") name: String) : List<Tag> {
        var searchResult = repository.findByNameContainingIgnoreCase(name)
        return searchResult.map {
            Tag(it.id, it.name)
        }
    }
}