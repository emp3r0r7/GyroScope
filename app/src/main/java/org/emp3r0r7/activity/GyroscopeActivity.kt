package org.emp3r0r7.activity

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.emp3r0r7.R
import org.emp3r0r7.network.WebSocketClient

class GyroscopeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationVectorSensor: Sensor

    private lateinit var yawTextView: TextView
    private lateinit var pitchTextView: TextView
    private lateinit var rollTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope)

        // Inizializza il SensorManager e il sensore di rotazione
        sensorManager = getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            rotationVectorSensor = it
        }

        // Associa i TextView alle loro rispettive viste nel layout
        yawTextView = findViewById(R.id.yawTextView)
        pitchTextView = findViewById(R.id.pitchTextView)
        rollTextView = findViewById(R.id.rollTextView)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            // Ottieni la matrice di rotazione dal vettore di rotazione
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            // Ottieni yaw, pitch e roll dalla matrice di rotazione
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            val yaw = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
            val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

            // Aggiorna i TextView con i nuovi valori
            yawTextView.text = "Yaw: %.2f".format(yaw)
            pitchTextView.text = "Pitch: %.2f".format(pitch)
            rollTextView.text = "Roll: %.2f".format(roll)

            // Invia i dati tramite WebSocket
            WebSocketClient.sendData("Roll: $roll, Pitch: $pitch, Yaw: $yaw")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
