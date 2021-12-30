package com.example.ping.web

import com.example.ping.application.PingService
import com.example.ping.domain.Ping
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class PingController(
    val service: PingService
) {

    @GetMapping("/ping")
    fun ping(request: HttpServletRequest): ResponseEntity<Ping> {
        val response = service.getPingResponse(request)
        return ResponseEntity.ok(response)
    }
}
