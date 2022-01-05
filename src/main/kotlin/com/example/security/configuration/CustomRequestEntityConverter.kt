package com.example.security.configuration

import org.springframework.core.convert.converter.Converter
import org.springframework.http.RequestEntity
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter
import org.springframework.util.MultiValueMap

class CustomRequestEntityConverter : Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<*>> {
    private val defaultConverter: OAuth2AuthorizationCodeGrantRequestEntityConverter = OAuth2AuthorizationCodeGrantRequestEntityConverter()

    @Suppress("UNCHECKED_CAST")
    override fun convert(req: OAuth2AuthorizationCodeGrantRequest): RequestEntity<*> {
        val entity = defaultConverter.convert(req)
        val params = entity!!.body as MultiValueMap<String, String>
        params.add("client_id", req.clientRegistration.clientId)
        return RequestEntity(
            params,
            entity.headers,
            entity.method,
            entity.url
        )
    }
}