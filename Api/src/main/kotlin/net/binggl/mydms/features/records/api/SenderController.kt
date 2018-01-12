package net.binggl.mydms.features.records.api

import net.binggl.mydms.features.records.data.SenderStore
import net.binggl.mydms.features.records.models.Sender
import net.binggl.mydms.infrastructure.security.ApiSecured
import net.binggl.mydms.shared.api.BaseResource
import net.binggl.mydms.shared.models.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SenderController(@Autowired private val store: SenderStore): BaseResource() {

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/senders")
    fun allSenders() : List<Sender> {
        return store.findAll()
    }

    @ApiSecured(requiredRole = Role.User)
    @GetMapping("/api/v1/senders/search")
    fun searchSenders(@RequestParam(value="name") name: String) : List<Sender> {
        return store.search(name)
    }
}