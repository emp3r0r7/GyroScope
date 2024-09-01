package org.emp3r0r7.activity

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.emp3r0r7.R
import org.emp3r0r7.network.WebSocketClient

class GyroscopeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor

    private lateinit var yawTextView: TextView
    private lateinit var pitchTextView: TextView
    private lateinit var rollTextView: TextView
    private var debugMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope)

        debugMode = intent.getBooleanExtra("debug", false)

        // Inizializza il SensorManager e il sensore di rotazione
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let { rotationVectorSensor = it }

        // Associa i TextView alle loro rispettive viste nel layout
        yawTextView = findViewById(R.id.yawTextView)
        pitchTextView = findViewById(R.id.pitchTextView)
        rollTextView = findViewById(R.id.rollTextView)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                WebSocketClient.close()
                finish()
            }
        })
    }


    private var filteredYaw = 0.0f
    private val alpha = 0.1f  // Coefficiente del filtro (puoi aggiustarlo in base alle necessità)

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            var yaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
            val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

            // Applica il filtro passa-basso per lo yaw
            filteredYaw = alpha * yaw + (1 - alpha) * filteredYaw
            yaw = filteredYaw

            val formattedYaw = String.format("%.6f", yaw)
            val formattedPitch = String.format("%.6f", pitch)
            val formattedRoll = String.format("%.6f", roll)

            // Aggiorna i TextView con i nuovi valori
            yawTextView.text = "Yaw (Z) : $formattedYaw"
            pitchTextView.text = "Pitch (X): $formattedPitch"
            rollTextView.text = "Roll (Y): $formattedRoll"

            // Invia i dati tramite WebSocket solo se non in debug mode
            if(!debugMode)
                WebSocketClient.sendData("Y=$formattedRoll|X=$formattedPitch|Z=$formattedYaw")
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            when (accuracy) {
                SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                    // Mostra un messaggio all'utente per calibrare il sensore
                    Toast.makeText(this, "Calibrare il dispositivo muovendolo in un pattern a forma di 8", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketClient.close()
        finish()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

}
