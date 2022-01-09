package com.example.twitter.util

import com.example.twitter.domain.ReferencedTweetType

const val TWITTER_QUERY_LIMIT = 512

enum class Operator(
    val literal: String
) {
    OR("OR"),
    FROM("from:"),
    URL("url:"),
    RETWEETS_OF("retweets_of:")
}

fun createUserQueries(
    userNames: List<String>,
    queryPrefix: String,
    separatingOperator: Operator,
    userPrefix: Operator,
    limit: Int
): List<String> {
    val prefixedUsers = userNames.map { "${userPrefix.literal}$it" }
    var usersLeft = prefixedUsers

    val result = ArrayList<String>()
    do {
        val (query, notJoinedUsers) = createQueryToLimit(
            usersLeft,
            queryPrefix,
            separatingOperator,
            limit
        )
        result.add(query)
        usersLeft = notJoinedUsers
    } while (usersLeft.isNotEmpty())

    return result
}

infix fun ReferencedTweetType.or(quote: ReferencedTweetType): String {
    return "${this.paramValue} OR ${quote.paramValue}"
}

infix fun String.or(quote: ReferencedTweetType): String {
    return "$this OR ${quote.paramValue}"
}

private fun createQueryToLimit(
    fromUsers: List<String>,
    queryPrefix: String,
    separatingOperator: Operator,
    limit: Int
): Pair<String, List<String>> {
    require(fromUsers.isNotEmpty())

    var currentQuery = "$queryPrefix (${fromUsers[0]}"

    var elementsTaken = 1
    for (name in fromUsers.drop(1)) {
        val additionalLength = name.length + separatingOperator.literal.length + 2 // spaces
        val potentialQueryLength = queryPrefix.length + currentQuery.length + additionalLength + 1 // bracket at the end
        if (potentialQueryLength > limit) {
            break
        }
        elementsTaken++
        currentQuery = "$currentQuery ${separatingOperator.literal} $name"
    }

    return "$currentQuery)" to fromUsers.drop(elementsTaken)
}
