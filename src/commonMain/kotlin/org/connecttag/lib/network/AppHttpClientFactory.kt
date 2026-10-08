package org.connecttag.lib.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

public object AppHttpClientFactory {
    public fun create(
        json: Json,
        headerAppender: NetworkRequestHeaderAppender,
        networkEventBus: NetworkEventBus,
        networkConfig: NetworkConfig = NetworkConfig(apiBaseUrl = ""),
        appLogger: AppLogger = NoOpAppLogger,
    ): HttpClient = HttpClient {
        install(createClientPlugin("StructuredHttpTelemetry") {
            on(Send) { request ->
                val startedAt = currentTimeMillis()
                val path = request.url.encodedPath
                val method = request.method.value
                val url = request.url.buildString()
                val baseAttributes = linkedMapOf(
                    "method" to method,
                    "url" to url,
                    "path" to path,
                )
                try {
                    val call = proceed(request)
                    val response = call.response
                    if (shouldLogRequest(request)) {
                        appLogger.info(
                            name = "http.client.response",
                            message = "HTTP ${response.status.value} $method $path",
                            category = AppLogCategory.NETWORK,
                            attributes = baseAttributes + mapOf(
                                "status" to response.status.value.toString(),
                                "duration_ms" to (currentTimeMillis() - startedAt).toString(),
                            ),
                        )
                    }
                    if (response.status.value == 403) {
                        networkEventBus.post(NetworkEvent.Forbidden)
                    }
                    call
                } catch (throwable: Throwable) {
                    if (shouldLogRequest(request)) {
                        appLogger.error(
                            name = "http.client.failure",
                            message = throwable.message ?: "HTTP request failed",
                            category = AppLogCategory.NETWORK,
                            attributes = baseAttributes + mapOf(
                                "duration_ms" to (currentTimeMillis() - startedAt).toString(),
                            ),
                            throwable = throwable,
                        )
                    }
                    throw throwable
                }
            }
        })
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = networkConfig.requestTimeoutMs
            connectTimeoutMillis = networkConfig.connectTimeoutMs
            socketTimeoutMillis = networkConfig.socketTimeoutMs
        }
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                !response.status.isSuccess() && response.status.value in 500..599
            }
            exponentialDelay()
        }

        defaultRequest {
            contentType(ContentType.Application.Json)

            runBlocking {
                headerAppender.appendTo(headers)
            }
        }
    }

    private fun shouldLogRequest(request: HttpRequestBuilder): Boolean {
        val encodedPath = request.url.encodedPath.lowercase()
        if (!encodedPath.contains("/api/v1/app-config/center")) return true
        return request.headers[ApiHeaders.LOG_ORIGIN]
            ?.equals("config-screen", ignoreCase = true) == true
    }

    private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()
}
