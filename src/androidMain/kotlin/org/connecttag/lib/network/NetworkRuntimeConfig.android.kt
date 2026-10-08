package org.connecttag.lib.network

actual object NetworkRuntimeConfig {
    actual fun apiBaseUrl(): String {
        return System.getenv("API_BASE_URL")
            ?: System.getProperty("api.baseUrl")
            ?: ServiceConstants.ANDROID_GATEWAY
    }

    actual fun bootstrapToken(): String? {
        return System.getenv("APP_BEARER_TOKEN")
            ?: System.getProperty("app.bearer.token")
    }

    actual fun apiKey(): String {
        return System.getenv("APP_API_KEY")
            ?: System.getProperty("app.api.key")
            ?: ""
    }

    actual fun internalToken(): String? {
        return System.getenv(ServiceConstants.ENV_INTERNAL_TOKEN)
            ?: System.getProperty(ServiceConstants.INTERNAL_TOKEN_PROP)
    }
}
