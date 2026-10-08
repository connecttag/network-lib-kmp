package org.connecttag.lib.network

public object AppApiConfigFactory {
    public fun create(
        baseUrlProvider: () -> String,
        apiKeyProvider: () -> String = { "" },
        tokenRefreshCoordinator: TokenRefreshCoordinator? = null,
    ): ApiConfig = ApiConfig(
        baseUrl = baseUrlProvider,
        apiKey = apiKeyProvider,
        autoRefreshOnUnauthorized = tokenRefreshCoordinator?.let { coordinator -> { coordinator.refresh() } },
    )
}
