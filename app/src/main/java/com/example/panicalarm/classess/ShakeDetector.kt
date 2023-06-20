package com.example.panicalarm

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.panicalarm.classess.Sensitivity

class ShakeDetector(private val context: Context, private val onShake: () -> Unit) : SensorEventListener {
   // private val accelerationThreshold = 10 // Adjust this value to control the sensitivity

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f
    lateinit var sensitivity: Sensitivity

    fun start() {
        sensitivity = Sensitivity()
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        sensorManager = null
        accelerometer = null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ

        lastX = x
        lastY = y
        lastZ = z

        val acceleration = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble())
        if (acceleration >= sensitivity.getSensitivity()) {
            Log.d("shakeDetect","shake")
            onShake.invoke()
        }
    }
}
