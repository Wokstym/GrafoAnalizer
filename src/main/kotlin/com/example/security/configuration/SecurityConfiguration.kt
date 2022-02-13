package com.example.security.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.client.RestTemplate
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration(
    var repository: ClientRegistrationRepository
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/login", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
//            .csrf { it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) } // todo fix csrf
            .oauth2Login()
            .defaultSuccessUrl("http://localhost:3000/login", true) // todo change localhost to redirect to domain from what login request came from
            .authorizationEndpoint()
            .authorizationRequestResolver(CustomAuthorizationRequestResolver(repository, "/oauth2/authorization"))
            .and()
            .tokenEndpoint()
            .accessTokenResponseClient(accessTokenResponseClient())

        http.csrf().disable()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000/")
        configuration.allowedMethods = listOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun accessTokenResponseClient(): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
        val tokenResponseHttpMessageConverter = OAuth2AccessTokenResponseHttpMessageConverter()
        val restTemplate = RestTemplate(listOf(FormHttpMessageConverter(), tokenResponseHttpMessageConverter))
        restTemplate.errorHandler = OAuth2ErrorResponseErrorHandler()

        val client = DefaultAuthorizationCodeTokenResponseClient()
        client.setRequestEntityConverter(CustomRequestEntityConverter())
        client.setRestOperations(restTemplate)
        return client
    }
}
