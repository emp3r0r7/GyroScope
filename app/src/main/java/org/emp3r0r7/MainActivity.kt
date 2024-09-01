package org.emp3r0r7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.WebSocketListener
import org.emp3r0r7.activity.GyroscopeActivity
import org.emp3r0r7.network.WebSocketClient
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {

    private lateinit var ipAddressEditText: EditText
    private lateinit var portEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var debugButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ipAddressEditText = findViewById(R.id.ipAddressEditText)
        portEditText = findViewById(R.id.portEditText)
        connectButton = findViewById(R.id.connectButton)
        debugButton = findViewById(R.id.debugButton)

        debugButton.setOnClickListener{

            val intent = Intent(this, GyroscopeActivity::class.java)
            intent.putExtra("debug", true)
            startActivity(intent)

        }

        connectButton.setOnClickListener {
            val ipAddress = ipAddressEditText.text.toString().trim()
            val port = portEditText.text.toString().trim()

            if (ipAddress.isNotEmpty() && port.isNotEmpty()) {
                try {
                    val portNumber = port.toInt()
                    tryWebSocketConnection(ipAddress, portNumber).thenAccept { connectionSuccessful ->
                        runOnUiThread {

                            if (connectionSuccessful) {
                                val intent = Intent(this, GyroscopeActivity::class.java)
                                intent.putExtra("debug", false)
                                startActivity(intent)
                            } else
                                Toast.makeText(this, "Failed to connect to WebSocket", Toast.LENGTH_SHORT).show()

                        }

                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid port number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both IP address and port", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tryWebSocketConnection(ipAddress: String, port: Int): CompletableFuture<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()

        WebSocketClient.start(ipAddress, port, object : WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
                completableFuture.complete(true)
            }

            override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
                completableFuture.complete(false)
            }
        })

        return completableFuture
    }
}
