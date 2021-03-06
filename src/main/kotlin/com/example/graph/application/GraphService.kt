package com.example.graph.application

import com.example.common.UnorderedPair
import com.example.graph.domain.GraphData
import com.example.graph.domain.TwitterRetweetEdge
import com.example.graph.domain.TwitterUserNode
import com.example.twitter.client.TwitterClient
import com.example.twitter.domain.User
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.stereotype.Service

@Service
class GraphService(
    val client: TwitterClient
) {

    fun generateFromSearch(names: List<String>, authorizedClient: OAuth2AuthorizedClient): GraphData {
        val tweetsData = client.getRetweetsFromUsers(names, authorizedClient)

        val users: Map<String, User> = client.getUsers(names, authorizedClient).associateBy { it.id }
        val referencedTweetsData = tweetsData.referencedTweetsData.associateBy { it.id }

        val edges = tweetsData.tweets
            .flatMap { originalTweet ->
                originalTweet.referencedTweets!!.map { referencedTweet ->
                    val referencedTweetInfo = referencedTweetsData[referencedTweet.id]
                    originalTweet.authorId to (
                        referencedTweetInfo?.authorId
                            ?: ""
                        ) // mentioned tweet can be deleted already, in that case we provide empty string, which will be filtered in next line anyway
                }
            }.filter { (_, mentionedTweetAuthorId) ->
                users.containsKey(mentionedTweetAuthorId)
            }.filter {
                it.second != it.first
            }.map { (tweetAuthorId, mentionedTweetAuthorId) ->
                UnorderedPair(tweetAuthorId, mentionedTweetAuthorId)
            }.groupingBy { it }
            .eachCount()
            .map { (authorPair, count) ->
                TwitterRetweetEdge(authorPair.first, authorPair.second, count)
            }

        val nodes = users.map { (_, user) ->
            TwitterUserNode(user.id, user.name, user.username, user.publicMetrics.followers)
        }

        return GraphData(nodes, edges)
    }

    fun generateFromList(listId: String, authorizedClient: OAuth2AuthorizedClient): GraphData {
        val info = client.getListInfo(listId, authorizedClient)

        val users: Map<String, User> = info.tweetAuthorsById
        val referencedTweetsData = info.referencedTweetsData.associateBy { it.id }

        val edges = info.tweets
            .filter { it.referencesAnyTweet }
            .flatMap { originalTweet ->
                originalTweet.referencedTweets!!.map { referencedTweet ->
                    val referencedTweetInfo = referencedTweetsData[referencedTweet.id]!!
                    originalTweet.authorId to referencedTweetInfo.authorId
                }
            }.filter { (_, mentionedTweetAuthorId) ->
                users.containsKey(mentionedTweetAuthorId)
            }.filter {
                it.second != it.first
            }.map { (tweetAuthorId, mentionedTweetAuthorId) ->
                UnorderedPair(tweetAuthorId, mentionedTweetAuthorId)
            }.groupingBy { it }
            .eachCount()
            .map { (authorPair, count) ->
                TwitterRetweetEdge(authorPair.first, authorPair.second, count)
            }

        val nodes = users.map { (_, user) ->
            TwitterUserNode(user.id, user.name, user.username, user.publicMetrics.followers)
        }

        return GraphData(nodes, edges)
    }
}
