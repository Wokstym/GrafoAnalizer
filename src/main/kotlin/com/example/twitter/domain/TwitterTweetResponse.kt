package com.example.twitter.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

data class TwitterTweetResponse(
    val data: List<TweetInfo>,
    val meta: MetaData,
    val includes: Includes
)

data class TwitterUserResponse(
    val data: List<User>
)

data class TweetInfo(
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

data class MetaData(
    @field:JsonProperty("next_token")
    val nextToken: String?,
    @field:JsonProperty("result_count")
    val resultCount: Int
)

data class ReferencedTweet(
    val id: String,
    val type: ReferencedTweetType
)

enum class ReferencedTweetType(
    @field:JsonValue
    val jsonValue: String,
    val paramValue: String
) {
    RETWEET("retweeted", "is:retweet"),
    QUOTE("quoted", "is:quote"),
    REPLY("replied_to", "is:reply");
}

data class Includes(
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
