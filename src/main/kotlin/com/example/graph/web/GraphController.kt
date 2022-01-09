package com.example.graph.web

import com.example.graph.application.GraphService
import com.example.graph.domain.GraphData
import com.example.security.application.OAuth2UserEntityDetails
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
class GraphController(
    val service: GraphService
) {
    private val log = LogManager.getLogger()

    @GetMapping("/list/{listId}")
    fun generateFromList(
        @PathVariable listId: String,
        @RegisteredOAuth2AuthorizedClient client: OAuth2AuthorizedClient,
        @AuthenticationPrincipal user: OAuth2UserEntityDetails
    ): ResponseEntity<GraphData> {
        log.info("Generating list with id=$listId, for user=${user.name}")
        val response = service.generateFromList(listId, client)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    fun generateFromSearch(
        @RequestBody @Valid request: GetGraphSearchRequest,
        @RegisteredOAuth2AuthorizedClient client: OAuth2AuthorizedClient,
        @AuthenticationPrincipal user: OAuth2UserEntityDetails
    ): ResponseEntity<GraphData> {
        log.info("Fetching for user=${user.name} tweets of ${request.userNames}")
        val response = service.generateFromSearch(request.userNames, client)
        return ResponseEntity.ok(response)
    }

    data class GetGraphSearchRequest(
        val userNames: List<@Size(max = 15) String>
    )

    // temporary to easy get cookie value after login to insert to postman
    @GetMapping("/")
    fun cookie(@CookieValue("JSESSIONID") token: String): String {
        return token
    }
}
