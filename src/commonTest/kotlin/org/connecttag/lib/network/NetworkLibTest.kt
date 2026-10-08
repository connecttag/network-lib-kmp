package org.connecttag.lib.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class NetworkLibTest {

    @Test
    fun testInMemoryAuthTokenProvider() = runTest {
        val provider = InMemoryAuthTokenProvider()
        assertNull(provider.getAccessToken())
        assertNull(provider.getRefreshToken())

        provider.setAccessToken("sample-access-token")
        provider.setRefreshToken("sample-refresh-token")
        assertEquals("sample-access-token", provider.getAccessToken())
        assertEquals("sample-refresh-token", provider.getRefreshToken())

        val getAuthToken = GetAuthTokenUseCase(provider)
        assertEquals("sample-access-token", getAuthToken())

        val clearTokens = ClearAuthTokenUseCase(provider)
        clearTokens()
        assertNull(provider.getAccessToken())
        assertNull(provider.getRefreshToken())
    }

    @Test
    fun testAppJsonFactory() {
        val json = AppJsonFactory.create()
        assertNotNull(json)
    }

    @Test
    fun testNetworkEventBus() = runTest {
        val bus = NetworkEventBus()
        assertNotNull(bus.events)
    }

    @Test
    fun testApiResponse() {
        val success = ApiResponse(success = true, data = "hello", message = "OK")
        assertEquals(true, success.success)
        assertEquals("hello", success.data)
        assertEquals("OK", success.message)

        val error = ApiResponse<String>(success = false, data = null, message = "Not Found")
        assertEquals(false, error.success)
        assertNull(error.data)
        assertEquals("Not Found", error.message)
    }
}
