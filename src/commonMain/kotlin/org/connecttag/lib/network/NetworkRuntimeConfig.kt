package org.connecttag.lib.network

expect object NetworkRuntimeConfig {
    fun apiBaseUrl(): String
    fun bootstrapToken(): String?
    fun apiKey(): String
    fun internalToken(): String?
}
