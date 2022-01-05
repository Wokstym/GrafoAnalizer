package com.example.security.configuration

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator
import org.springframework.security.crypto.keygen.StringKeyGenerator
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.servlet.http.HttpServletRequest

class CustomAuthorizationRequestResolver(
    repo: ClientRegistrationRepository,
    authorizationRequestBaseUri: String
) : OAuth2AuthorizationRequestResolver {

    private val defaultResolver: OAuth2AuthorizationRequestResolver
    private val secureKeyGenerator: StringKeyGenerator =
        Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96)

    init {
        defaultResolver = DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri)
    }

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val req = defaultResolver.resolve(request)
        return customizeAuthorizationRequest(req)
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        val req = defaultResolver.resolve(request, clientRegistrationId)
        return customizeAuthorizationRequest(req)
    }

    private fun customizeAuthorizationRequest(req: OAuth2AuthorizationRequest?): OAuth2AuthorizationRequest? {
        req ?: return null

        val attributes = HashMap(req.attributes)
        val additionalParameters = HashMap(req.additionalParameters)

        addPkceParameters(attributes, additionalParameters)

        return OAuth2AuthorizationRequest
            .from(req)
            .attributes(attributes)
            .additionalParameters(additionalParameters)
            .build()
    }

    private fun addPkceParameters(attributes: MutableMap<String, Any>, additionalParameters: MutableMap<String, Any>) {
        val codeVerifier = secureKeyGenerator.generateKey()
        attributes[CODE_VERIFIER] = codeVerifier
        try {
            val codeChallenge = createHash(codeVerifier)
            additionalParameters[CODE_CHALLENGE] = codeChallenge
            additionalParameters[CODE_CHALLENGE_METHOD] = "S256"
        } catch (e: NoSuchAlgorithmException) {
            additionalParameters[CODE_CHALLENGE] = codeVerifier
        }
    }

    private fun createHash(value: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(value.toByteArray(StandardCharsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}
