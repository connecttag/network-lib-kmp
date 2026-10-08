package org.connecttag.lib.network

import kotlin.concurrent.Volatile

interface AuthTokenProvider {
    fun getAccessToken(): String?
    fun setAccessToken(token: String?)
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String?)

    fun clearTokens() {
        setAccessToken(null)
        setRefreshToken(null)
    }

    // Backward-compatible aliases used by existing callers.
    fun getToken(): String? = getAccessToken()
    fun setToken(token: String?) = setAccessToken(token)
}

class InMemoryAuthTokenProvider(
    initialAccessToken: String? = null,
    initialRefreshToken: String? = null,
) : AuthTokenProvider {
    @Volatile
    private var accessToken: String? = initialAccessToken?.takeIf { it.isNotBlank() }

    @Volatile
    private var refreshToken: String? = initialRefreshToken?.takeIf { it.isNotBlank() }

    override fun getAccessToken(): String? = accessToken

    override fun setAccessToken(token: String?) {
        accessToken = token?.takeIf { it.isNotBlank() }
    }

    override fun getRefreshToken(): String? = refreshToken

    override fun setRefreshToken(token: String?) {
        refreshToken = token?.takeIf { it.isNotBlank() }
    }
}
