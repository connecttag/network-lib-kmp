package org.connecttag.lib.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class NetworkEvent {
    data object Unauthorized : NetworkEvent()
    data object Forbidden : NetworkEvent()
    data class Error(val message: String) : NetworkEvent()
}

class NetworkEventBus {
    private val _events = MutableSharedFlow<NetworkEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<NetworkEvent> = _events.asSharedFlow()

    fun post(event: NetworkEvent) {
        _events.tryEmit(event)
    }
}
