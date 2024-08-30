package org.emp3r0r7.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object WebSocketClient {

    private const val TAG = "WebSocketClient"

    private lateinit var webSocket: WebSocket
    private lateinit var request: Request
    private var reconnecting = false

    private val handler = Handler(Looper.getMainLooper())
    private val reconnectRunnable = Runnable { checkEndpointAndReconnect() }
    private val executor = Executors.newSingleThreadExecutor()

    fun start(ipAddress: String, port: Int, listener: WebSocketListener) {
        Log.d(TAG, "Starting WebSocket connection to ws://$ipAddress:$port/sensorData")

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        val url = "ws://$ipAddress:$port/sensorData"
        request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d(TAG, "WebSocket connected")
                reconnecting = false
                listener.onOpen(webSocket, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "WebSocket received message: $text")
                listener.onMessage(webSocket, text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e(TAG, "WebSocket connection failed: ${t.message}")
                listener.onFailure(webSocket, t, response)
                scheduleReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed with reason: $reason")
                listener.onClosed(webSocket, code, reason)
                scheduleReconnect()
            }
        })
    }

    fun sendData(message: String) {
        if (::webSocket.isInitialized) {
            //Log.d(TAG, "Sending data: $message")
            webSocket.send(message)
        } else {
            Log.w(TAG, "WebSocket not initialized, unable to send data")
        }
    }

    fun close() {
        if (::webSocket.isInitialized) {
            Log.d(TAG, "Closing WebSocket connection")
            webSocket.close(1000, "Goodbye!")
            handler.removeCallbacks(reconnectRunnable)
        } else {
            Log.w(TAG, "WebSocket not initialized, nothing to close")
        }
    }

    private fun scheduleReconnect() {
        if (!reconnecting) {
            Log.d(TAG, "Scheduling reconnection attempt in 5 seconds")
            reconnecting = true
            handler.postDelayed(reconnectRunnable, 5000) // Controlla l'endpoint ogni 5 secondi
        }
    }

    private fun checkEndpointAndReconnect() {
        val ipAddress = request.url.host
        val port = request.url.port

        executor.execute {
            if (isEndpointReachable(ipAddress, port)) {
                Log.d(TAG, "Endpoint is reachable, attempting to reconnect")
                close() // Chiudi il WebSocket esistente prima di riconnetterti
                reconnect()
            } else {
                Log.w(TAG, "Endpoint not reachable, retrying in 5 seconds")
                handler.postDelayed(reconnectRunnable, 5000)
            }
        }
    }

    private fun isEndpointReachable(ipAddress: String, port: Int): Boolean {
        return try {
            Socket(ipAddress, port).use {
                Log.d(TAG, "Successfully connected to $ipAddress:$port")
                true // La connessione è riuscita
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to $ipAddress:$port: ${e.message}")
            false // La connessione è fallita
        }
    }

    private fun reconnect() {
        Log.d(TAG, "Reconnecting to WebSocket")
        start(request.url.host, request.url.port, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d(TAG, "Reconnection successful")
                reconnecting = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e(TAG, "Reconnection failed: ${t.message}")
                scheduleReconnect() // Se fallisce nuovamente, riprogramma un altro tentativo
            }
        })
    }
}
