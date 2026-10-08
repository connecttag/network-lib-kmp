package org.connecttag.lib.network

import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders

class NetworkRequestHeaderAppender(
    private val headerProvider: NetworkHeaderProvider,
    private val getAuthToken: GetAuthTokenUseCase,
) {
    suspend fun appendTo(headers: HeadersBuilder) {
        headerProvider.getCommonHeaders().forEach { (key, value) ->
            headers.append(key, value)
        }

        val token = getAuthToken()
        if (!token.isNullOrBlank() && headers[HttpHeaders.Authorization] == null) {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
