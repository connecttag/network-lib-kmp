package org.connecttag.lib.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenRefreshCoordinator(
    private val httpClient: HttpClient,
    private val baseUrlProvider: () -> String,
    private val getRefreshToken: GetRefreshTokenUseCase,
    private val setAuthToken: SetAuthTokenUseCase,
    private val setRefreshToken: SetRefreshTokenUseCase,
    private val clearAuthToken: ClearAuthTokenUseCase,
) {
    private val refreshMutex = Mutex()

    suspend fun refresh(): Boolean = refreshMutex.withLock {
        val refreshToken = getRefreshToken().orEmpty()
        if (refreshToken.isBlank()) {
            clearAuthToken()
            return@withLock false
        }

        val baseUrl = baseUrlProvider()
        val refreshUrl = "${baseUrl.trimEnd('/')}${ApiEndpoints.AUTH_REFRESH}"
        val response = runCatching {
            httpClient.post(refreshUrl) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken = refreshToken))
            }.body<ApiResponse<AuthTokensResponse>>()
        }.getOrElse {
            clearAuthToken()
            return@withLock false
        }

        val payload = response.data
        if (!response.success || payload == null) {
            clearAuthToken()
            return@withLock false
        }

        setAuthToken(payload.accessToken)
        setRefreshToken(payload.refreshToken)
        true
    }
}
