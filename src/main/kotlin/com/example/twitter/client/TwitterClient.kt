package com.example.twitter.client

import com.example.common.HttpEntityBuilder
import com.example.twitter.domain.*
import com.example.twitter.domain.ReferencedTweetType.QUOTE
import com.example.twitter.domain.ReferencedTweetType.RETWEET
import com.example.twitter.util.Operator.*
import com.example.twitter.util.TWITTER_QUERY_LIMIT
import com.example.twitter.util.createUserQueries
import com.example.twitter.util.or
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
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
        val data: MutableList<TweetInfo> = response.data?.toMutableList() ?: ArrayList()
        val users: MutableSet<User> = response.includes?.users?.toMutableSet() ?: HashSet()
        val referencedTweetsData: MutableList<TweetInfo> =
            response.includes?.referencedTweetsData?.toMutableList() ?: ArrayList()
        var count = 1

        while (!data.last().createdAt.isBefore(maxDate) && response.meta.nextToken != null) {
            response = restTemplate.getTweetsFromList(listId, accessToken, response.meta.nextToken)
            data += response.data ?: emptyList()
            users += response.includes?.users ?: emptyList()
            referencedTweetsData += response.includes?.referencedTweetsData ?: emptyList()
            count += 1
        }

        val inRangeData = data.filter { it.createdAt.isAfter(maxDate) }

        log.info("Made $count requests, tweets size: ${inRangeData.size}")

        return ListInfoData(inRangeData, users.toList(), referencedTweetsData)
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

    fun getUsers(
        userNames: List<String>,
        client: OAuth2AuthorizedClient
    ): List<User> {
        log.info("Making ${userNames.size / 100} requests for users")
        return userNames.chunked(100)
            .flatMap { restTemplate.users(it, client.accessToken.tokenValue).data }
    }

    fun getRetweetsFromUsers(
        userNames: List<String>,
        client: OAuth2AuthorizedClient
    ): ListInfoData {
        require(userNames.isNotEmpty())

        val accessToken = client.accessToken.tokenValue

        val queries = createUserQueries(
            userNames = userNames,
            queryPrefix = "(${RETWEET or QUOTE})",
            separatingOperator = OR,
            userPrefix = FROM,
            limit = TWITTER_QUERY_LIMIT
        )

        log.info("Prepared ${queries.size} queries: $queries")

        val data = HashSet<TweetInfo>()
        val users = HashSet<User>()
        val referencedTweetsData = HashSet<TweetInfo>()
        var count = 0

        for (query in queries) {
            var nextPageToken: String? = null
            do {
                val response = restTemplate.searchTweets(query, accessToken, nextPageToken)
                count++
                data += response.data ?: emptyList()
                users += response.includes?.users ?: emptyList()
                referencedTweetsData += response.includes?.referencedTweetsData ?: emptyList()
                nextPageToken = response.meta.nextToken
            } while (nextPageToken != null)
        }

        log.info("Made $count requests, tweets size: ${data.size}")

        return ListInfoData(data.toList(), users.toList(), referencedTweetsData.toList())
    }

    private fun RestTemplate.users(userNames: List<String>, accessToken: String): TwitterUserResponse {
        require(userNames.size in 1..100)

        val requestEntity = HttpEntityBuilder.noBody()
            .bearerAuthorization(accessToken)
            .build()

        val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/users/by")
            .queryParam("usernames", userNames.joinToString(separator = ","))
            .queryParam("user.fields", "public_metrics")
            .toUriString()

        val exchange =
            exchange(url, HttpMethod.GET, requestEntity, TwitterUserResponse::class.java)

        if (exchange.statusCode != HttpStatus.OK || exchange.body == null) {
            throw IllegalArgumentException("Bad request, server response: $exchange")
        }
        return exchange.body!!
    }

    private fun RestTemplate.searchTweets(
        query: String,
        accessToken: String,
        nextPageToken: String? = null
    ): TwitterTweetResponse {
        val requestEntity = HttpEntityBuilder.noBody()
            .bearerAuthorization(accessToken)
            .build()

        val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/tweets/search/recent")
            .queryParam("query", query)
            .queryParam("expansions", "author_id,referenced_tweets.id")
            .queryParam("tweet.fields", "author_id,referenced_tweets,created_at")
            .queryParam("user.fields", "public_metrics")
            .queryParam("max_results", 100)

        if (nextPageToken != null) {
            url.queryParam("next_token", nextPageToken)
        }

        val exchange =
            exchange(url.build(false).toUri(), HttpMethod.GET, requestEntity, TwitterTweetResponse::class.java)

        if (exchange.statusCode != HttpStatus.OK || exchange.body == null) {
            throw IllegalArgumentException("Bad request, server response: $exchange")
        }
        return exchange.body!!
    }
}
