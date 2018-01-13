package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.data.TagStore
import net.binggl.mydms.features.records.model.Tag
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(@Autowired private val store: TagStore): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/tags")
    fun allTags() : List<Tag> {
        return store.findAll()
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/tags/search")
    fun searchTags(@RequestParam(value="name") name: String) : List<Tag> {
        return store.search(name)
    }
}