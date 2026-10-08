package org.connecttag.lib.network

import kotlinx.serialization.Serializable

@Serializable
public data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val message: String? = null,
)

@Serializable
public data class AuthTokensResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
public data class RefreshTokenRequest(
    val refreshToken: String,
)

public data class NetworkConfig(
    val apiBaseUrl: String,
    val connectTimeoutMs: Long = 15_000,
    val requestTimeoutMs: Long = 30_000,
    val socketTimeoutMs: Long = 30_000,
)

public fun interface NetworkHeaderProvider {
    public fun getCommonHeaders(): Map<String, String>
}

public object ServiceConstants {
    public const val ANDROID_GATEWAY: String = "http://10.0.2.2:8080"
    public const val LOCAL_GATEWAY: String = "http://127.0.0.1:8080"
    public const val ENV_INTERNAL_TOKEN: String = "APP_INTERNAL_TOKEN"
    public const val INTERNAL_TOKEN_PROP: String = "app.internal.token"
}

public object ApiEndpoints {
    public const val AUTH_REFRESH: String = "/api/v1/auth/refresh"
}

public object ApiHeaders {
    public const val LOG_ORIGIN: String = "X-Log-Origin"
}

public enum class AppLogCategory {
    NETWORK,
    GENERAL,
}

public interface AppLogger {
    public fun info(name: String, message: String, category: AppLogCategory, attributes: Map<String, String>)
    public fun error(name: String, message: String, category: AppLogCategory, attributes: Map<String, String>, throwable: Throwable?)
}

public object NoOpAppLogger : AppLogger {
    override fun info(name: String, message: String, category: AppLogCategory, attributes: Map<String, String>) {}
    override fun error(name: String, message: String, category: AppLogCategory, attributes: Map<String, String>, throwable: Throwable?) {}
}

public data class ApiConfig(
    val baseUrl: () -> String,
    val apiKey: () -> String = { "" },
    val autoRefreshOnUnauthorized: (suspend () -> Boolean)? = null,
)
