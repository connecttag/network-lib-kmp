package org.connecttag.lib.network

import kotlinx.serialization.json.Json

object AppJsonFactory {
    fun create(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}

