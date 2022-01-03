package com.example.common

import java.util.*
import kotlin.math.max
import kotlin.math.min

class UnorderedPair<T>(
    val first: T,
    val second: T
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UnorderedPair<*>

        return (other.first == first && other.second == second) ||
                (other.first == second && other.second == first)
    }

    override fun hashCode(): Int {
        val hashFirst = first.hashCode()
        val hashSecond = second.hashCode()
        val maxHash = max(hashFirst, hashSecond)
        val minHash = min(hashFirst, hashSecond)
        return Objects.hash(minHash, maxHash)
    }

    override fun toString() = "($first, $second)"

    operator fun component1(): T = first
    operator fun component2(): T = second
}