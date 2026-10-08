package org.connecttag.lib.network

actual object NetworkRuntimeConfig {
    actual fun apiBaseUrl(): String = "http://localhost:8080"

    actual fun bootstrapToken(): String? = null

    actual fun apiKey(): String = ""

    actual fun internalToken(): String? = null
}

