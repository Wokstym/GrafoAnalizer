package com.example.ping.application

import com.example.ping.domain.Ping
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@Service
class PingService(
    @Value("\${app.version}")
    val version: String,
    @Value("\${app.name}")
    val name: String
) {

    fun getPingResponse(request: HttpServletRequest): Ping {
        return Ping(
            name = name,
            version = version,
            time = LocalDateTime.now(),
            senderIpAddress = request.remoteAddr
        )
    }
}
