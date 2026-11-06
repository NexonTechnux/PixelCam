package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2

class HorizonSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _rollAngle = MutableStateFlow(0f)
    val rollAngle: StateFlow<Float> = _rollAngle.asStateFlow()

    private val _isLevel = MutableStateFlow(false)
    val isLevel: StateFlow<Boolean> = _isLevel.asStateFlow()

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    fun start() {
        rotationSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        var roll = 0f
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            // Roll is orientationAngles[2], convert to degrees
            roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val ax = event.values[0]
            val ay = event.values[1]
            roll = Math.toDegrees(atan2(ax.toDouble(), ay.toDouble())).toFloat()
        }

        // Normalize roll around portrait orientation
        val normalizedRoll = when {
            roll > 90f -> 180f - roll
            roll < -90f -> -180f - roll
            else -> roll
        }

        _rollAngle.value = normalizedRoll
        _isLevel.value = kotlin.math.abs(normalizedRoll) <= 1.0f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
