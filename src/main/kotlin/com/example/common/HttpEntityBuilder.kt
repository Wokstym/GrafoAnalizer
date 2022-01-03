package com.example.common

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

class HttpEntityBuilder<T>(
    private val body: T? = null
) {
    private val headers = HttpHeaders()

    companion object {
        fun noBody() = HttpEntityBuilder<Nothing>()
        fun <T> body(body: T) = HttpEntityBuilder(body)
    }

    fun header(headerName: String, headerValue: String?): HttpEntityBuilder<T> {
        headers.set(
            headerName,
            headerValue
        )
        return this
    }

    fun authorization(token: String) = header("Authorization", token)

    fun build() = HttpEntity<T>(body, headers)
}

