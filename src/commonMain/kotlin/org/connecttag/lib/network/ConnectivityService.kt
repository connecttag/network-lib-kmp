package org.connecttag.lib.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class ServiceStatus(
    val serviceName: String,
    val isAvailable: Boolean,
    val latencyMs: Long = -1,
    val lastChecked: Long = 0,
    val error: String? = null,
)

class ConnectivityService(
    private val client: HttpClient,
    val gatewayUrl: String,
    val adminServiceUrl: String,
) {
    private val _statusFlow = MutableStateFlow<Map<String, ServiceStatus>>(emptyMap())
    val statusFlow: StateFlow<Map<String, ServiceStatus>> = _statusFlow.asStateFlow()

    private var monitoringJob: Job? = null

    fun startMonitoring(intervalMs: Long = 10_000) {
        monitoringJob?.cancel()
        monitoringJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                checkAllServices()
                delay(intervalMs)
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }

    suspend fun checkAllServices() {
        _statusFlow.value = mapOf(
            "Gateway" to pingService("Gateway", gatewayUrl),
            "Admin" to pingService("Admin", adminServiceUrl),
        )
    }

    private suspend fun pingService(name: String, url: String): ServiceStatus {
        val startTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
        return try {
            val response: HttpResponse = client.get(url) {
                timeout { requestTimeoutMillis = 3_000 }
            }
            val endTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
            ServiceStatus(
                serviceName = name,
                isAvailable = response.status.isSuccess(),
                latencyMs = endTime - startTime,
                lastChecked = endTime,
            )
        } catch (e: Exception) {
            ServiceStatus(
                serviceName = name,
                isAvailable = false,
                error = e.message,
                lastChecked = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            )
        }
    }
}

