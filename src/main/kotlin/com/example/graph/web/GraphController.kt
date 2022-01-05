package com.example.graph.web

import com.example.graph.application.GraphService
import com.example.graph.domain.GraphData
import com.example.security.application.OAuth2UserEntityDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GraphController(
    val service: GraphService
) {

    @GetMapping("/list/{listId}")
    fun generateFromList(
        @PathVariable listId: String,
        @RegisteredOAuth2AuthorizedClient client: OAuth2AuthorizedClient,
        @AuthenticationPrincipal user: OAuth2UserEntityDetails
    ): ResponseEntity<GraphData> {
        val response = service.generateFromList(listId, client)
        return ResponseEntity.ok(response)
    }
}
