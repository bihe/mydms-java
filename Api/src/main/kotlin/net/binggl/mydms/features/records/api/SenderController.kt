package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.entity.SenderEntity
import net.binggl.mydms.features.records.model.Sender
import net.binggl.mydms.features.records.repository.SenderRepository
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sender")
class SenderController(@Autowired private val repository: SenderRepository): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("")
    fun allSenders() : List<Sender> {
        var entities = repository.findAll()?.toList() ?: emptyList<SenderEntity>()
        return entities.map {
            Sender(it.id, it.name)
        }
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/search")
    fun searchSenders(@RequestParam(value="name") name: String) : List<Sender> {
        var searchResult = repository.findByNameContainingIgnoreCase(name)
        return searchResult.map {
            Sender(it.id, it.name)
        }
    }
}