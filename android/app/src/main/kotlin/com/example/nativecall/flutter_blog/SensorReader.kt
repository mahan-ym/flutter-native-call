package com.example.nativecall.flutter_blog

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel

class SensorReader: EventChannel.StreamHandler, SensorEventListener{
    private var sensorEventSink: EventChannel.EventSink? = null
    private lateinit var pressureSensor:Sensor
    private lateinit var sensorManager:SensorManager

    fun initialize(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager ;
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        sensorEventSink = events
    }

    override fun onCancel(arguments: Any?) {
        sensorEventSink = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        sensorEventSink?.success(event?.values?.get(0))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}
