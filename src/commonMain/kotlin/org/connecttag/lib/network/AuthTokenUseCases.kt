package org.connecttag.lib.network

class GetAuthTokenUseCase(
    private val authTokenProvider: AuthTokenProvider,
) {
    operator fun invoke(): String? = authTokenProvider.getAccessToken()
}

class SetAuthTokenUseCase(
    private val authTokenProvider: AuthTokenProvider,
) {
    operator fun invoke(token: String?) {
        authTokenProvider.setAccessToken(token)
    }
}

class GetRefreshTokenUseCase(
    private val authTokenProvider: AuthTokenProvider,
) {
    operator fun invoke(): String? = authTokenProvider.getRefreshToken()
}

class SetRefreshTokenUseCase(
    private val authTokenProvider: AuthTokenProvider,
) {
    operator fun invoke(token: String?) {
        authTokenProvider.setRefreshToken(token)
    }
}

class ClearAuthTokenUseCase(
    private val authTokenProvider: AuthTokenProvider,
) {
    operator fun invoke() {
        authTokenProvider.clearTokens()
    }
}
