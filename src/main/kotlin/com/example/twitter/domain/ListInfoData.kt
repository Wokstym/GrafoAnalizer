package com.example.twitter.domain

class ListInfoData(
    val tweets: List<TweetInfo>,
    val tweetAuthors: List<User>,
    val referencedTweetsData: List<TweetInfo>
) {
    val tweetAuthorsById: Map<String, User>
        get() = tweetAuthors.associateBy { it.id }
}
