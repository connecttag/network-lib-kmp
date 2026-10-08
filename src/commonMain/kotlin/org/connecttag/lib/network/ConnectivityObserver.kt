package org.connecttag.lib.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val status: Flow<Status>

    fun isLocalHost(host: String): Boolean = LocalHostMatcher.isLocalHost(host)

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

object LocalHostMatcher {
    fun isLocalHost(host: String): Boolean {
        val normalized = host.lowercase()
        if (
            normalized == "localhost" ||
            normalized == "127.0.0.1" ||
            normalized == "::1" ||
            normalized.startsWith("fe80:") ||
            normalized.startsWith("fc") ||
            normalized.startsWith("fd") ||
            normalized.endsWith(".local") ||
            '.' !in normalized
        ) return true

        val octets = normalized.split('.')
        if (octets.size != 4) return false
        val first = octets[0].toIntOrNull() ?: return false
        val second = octets[1].toIntOrNull() ?: return false

        return when {
            first == 10 -> true
            first == 127 -> true
            first == 169 && second == 254 -> true
            first == 192 && second == 168 -> true
            first == 172 && second in 16..31 -> true
            else -> false
        }
    }
}

