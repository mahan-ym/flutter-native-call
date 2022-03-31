import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
    @override
    State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  double sensorVal = 0;
  int batteryLevel = -1;

  static const MethodChannel _channel = MethodChannel('Example');
  static const EventChannel _sensorDataEventChannel = EventChannel('SensorReader');

  Future<void> getBatteryLevel() async {
    batteryLevel = await _channel.invokeMethod('getBattery');
    setState(() {});
  }

  Future<void> initSensorReader() async {
    return await _channel.invokeMethod('initializeSensor');
  }

  static Stream<double> setSensorListener() async* {
      yield* _sensorDataEventChannel.receiveBroadcastStream().asyncMap<double>((sensorData) => sensorData);
  }

  @override
  void initState() {
    super.initState();
    initSensorReader();

    setSensorListener().listen((sensorData) => {
      setState(() {
        sensorVal = sensorData;
      })
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Native call example app'),
        ),
        body: Center(child: Column(children: <Widget>[
          Container(
              margin: const EdgeInsets.all(20),
              child: Text('Sensor Data is: $sensorVal')
          ),
          Container(
              margin: const EdgeInsets.all(20),
              child: Text('Battery level is: $batteryLevel')
          )
        ])),
        floatingActionButton: FloatingActionButton(
          onPressed: getBatteryLevel,
          tooltip: 'get battery level',
        ),
      ),
    );
  }
}
