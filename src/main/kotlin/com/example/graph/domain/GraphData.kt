package com.example.graph.domain

data class GraphData(
    val nodes: List<TwitterUserNode>,
    val edges: List<TwitterRetweetEdge>
)

data class TwitterUserNode(
    val id: String,
    val name: String,
    val username: String,
    val followers: Int
)

data class TwitterRetweetEdge(
    val fromUserId: String,
    val toUserId: String,
    val retweets: Int
)