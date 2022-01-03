package com.example.graph.web

import com.example.graph.application.GraphService
import com.example.graph.domain.GraphData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GraphController(
    val service: GraphService
) {

    @GetMapping("/list/{listId}")
    fun generateFromList(@PathVariable listId: String): ResponseEntity<GraphData> {
        val response = service.generateFromList(listId)
        return ResponseEntity.ok(response)
    }
}
