package com.example.nativecall.flutter_blog

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity(), MethodChannel.MethodCallHandler {
    private lateinit var methodChannel:MethodChannel
    private val sensorReader = SensorReader()

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "Example")
        EventChannel(flutterEngine.dartExecutor.binaryMessenger, "SensorReader").setStreamHandler(sensorReader)
        methodChannel.setMethodCallHandler(this);
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (call.method == "getBattery") {
            val batteryLevel = getBatteryLevel()
            if (batteryLevel != -1 ) result.success(batteryLevel)
            else result.error("NOTAVAILABLE", "Battery level is not available", null)
            return
        }
        else if (call.method == "initializeSensor") {
            sensorReader.initialize(context)
            result.success(true)
            return
        }
    }

    private fun getBatteryLevel():Int {
        var batteryLevel = -1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES. LOLLIPOP) {
            val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            val intent = ContextWrapper(applicationContext)
                .registerReceiver(null, IntentFilter (Intent.ACTION_BATTERY_CHANGED))
            batteryLevel = (intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)?.times(100))?.div(
                intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    - 1)
            )!!
        }
        return batteryLevel
    }
}
