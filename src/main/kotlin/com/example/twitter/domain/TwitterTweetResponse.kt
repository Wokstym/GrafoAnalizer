package com.example.twitter.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class TwitterTweetResponse(
    val data: List<TweetInfo>,
    val meta: MetaData,
    val includes: Includes
)

class TweetInfo(
    @field:JsonProperty("author_id")
    val authorId: String,
    @field:JsonProperty("created_at")
    val createdAt: LocalDateTime,
    val id: String,
    @field:JsonProperty("referenced_tweets")
    val referencedTweets: List<ReferencedTweet>?,
    val text: String
) {
    val referencesAnyTweet: Boolean
        get() = referencedTweets?.isNotEmpty() ?: false
}

class MetaData(
    @field:JsonProperty("next_token")
    val nextToken: String?,
    @field:JsonProperty("result_count")
    val resultCount: Int
)

class ReferencedTweet(
    val id: String,
    val type: ReferencedTweetType
)

enum class ReferencedTweetType {
    retweeted, quoted, replied_to;
}

class Includes(
    val users: List<User>,
    @field:JsonProperty("tweets")
    val referencedTweetsData: List<TweetInfo>
)

data class User(
    val id: String,
    val name: String,
    val username: String,
    @field:JsonProperty("public_metrics")
    val publicMetrics: PublicMetrics
)

data class PublicMetrics(
    @field:JsonProperty("followers_count")
    val followers: Int,
    @field:JsonProperty("following_count")
    val following: Int,
    @field:JsonProperty("tweet_count")
    val tweets: Int,
    @field:JsonProperty("listed_count")
    val listedCount: Int
)
