package com.example.twitter.client

import com.example.common.HttpEntityBuilder
import com.example.twitter.domain.ListInfoData
import com.example.twitter.domain.TweetInfo
import com.example.twitter.domain.TwitterTweetResponse
import com.example.twitter.domain.User
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@Component
class TwitterClient(
    @Value("\${twitter.url:https://api.twitter.com/2/}")
    private val baseUrl: String,
    val restTemplate: RestTemplate
) {

    private val log = LogManager.getLogger()

    fun getListInfo(listId: String, client: OAuth2AuthorizedClient): ListInfoData {

        val maxDate = LocalDateTime.now().minusMonths(1)

        val accessToken = client.accessToken.tokenValue
        var response = restTemplate.getTweetsFromList(listId, accessToken)
        val data: MutableList<TweetInfo> = response.data.toMutableList()
        val users: MutableList<User> = response.includes.users.toMutableList()
        val referencedTweetsData: MutableList<TweetInfo> = response.includes.referencedTweetsData.toMutableList()
        var count = 1

        while (!data.last().createdAt.isBefore(maxDate)) {
            response = restTemplate.getTweetsFromList(listId, accessToken, response.meta.nextToken)
            data += response.data
            users += response.includes.users
            referencedTweetsData += response.includes.referencedTweetsData
            count += 1
        }

        val inRangeData = data.filter { it.createdAt.isAfter(maxDate) }

        log.info("Made $count requests, tweets size: ${inRangeData.size}")

        return ListInfoData(inRangeData, users, referencedTweetsData)
    }

    private fun RestTemplate.getTweetsFromList(
        listId: String,
        accessToken: String,
        nextPageToken: String? = null
    ): TwitterTweetResponse {
        val requestEntity = HttpEntityBuilder.noBody()
            .bearerAuthorization(accessToken)
            .build()

        val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/lists/$listId/tweets")
            .queryParam("expansions", "author_id,referenced_tweets.id")
            .queryParam("tweet.fields", "author_id,referenced_tweets,created_at")
            .queryParam("user.fields", "public_metrics")

        if (nextPageToken != null) {
            url.queryParam("pagination_token", nextPageToken)
        }

        val exchange = exchange(url.toUriString(), HttpMethod.GET, requestEntity, TwitterTweetResponse::class.java)

        if (exchange.statusCode != HttpStatus.OK || exchange.body == null) {
            throw IllegalArgumentException("Bad request, server response: $exchange")
        }
        return exchange.body!!
    }
}
