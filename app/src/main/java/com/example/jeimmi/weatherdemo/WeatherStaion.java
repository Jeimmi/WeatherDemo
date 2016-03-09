package com.example.jeimmi.weatherdemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherStaion extends Activity{
    private SensorManager sensorManager;
    private TextView temperatureTextView;
    private TextView pressureTextView;
    private TextView lightTextView;

    private float currentTemperture = Float.NaN;
    private float currentPressure = Float.NaN;
    private float currentLight = Float.NaN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        temperatureTextView = (TextView)findViewById(R.id.temperature);
        pressureTextView = (TextView)findViewById(R.id.pressure);
        lightTextView = (TextView)findViewById(R.id.light);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("weatherUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                updateGUI();
            }
        },0,1000);
    }

    private final SensorEventListener tempSensorEvenListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentTemperture = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    private final SensorEventListener pressureSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentPressure = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    private final SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentLight = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    protected void onResume(){
        super.onResume();

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor!=null)
            sensorManager.registerListener(lightSensorEventListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        else
            lightTextView.setText("Light Sensor Unavailable");

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor!=null)
            sensorManager.registerListener(pressureSensorEventListener,
                    pressureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        else
            pressureTextView.setText("Barometer Unavailable");

        Sensor temperatureSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if(temperatureSensor != null)
            sensorManager.registerListener(tempSensorEvenListener,
                    temperatureSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        else
            temperatureTextView.setText("Thermometer Unavailable");
    }

    protected void onPause(){
        sensorManager.unregisterListener(pressureSensorEventListener);
        sensorManager.unregisterListener(tempSensorEvenListener);
        sensorManager.unregisterListener(lightSensorEventListener);
        super.onPause();
    }

    private void updateGUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!Float.isNaN(currentPressure)){
                    pressureTextView.setText(currentPressure+"(mBras)");
                    pressureTextView.invalidate();
                }
                if(!Float.isNaN(currentLight)){
                    String lightStr = "Sunny";
                    if(currentLight<= SensorManager.LIGHT_CLOUDY)
                        lightStr = "Night";
                    else if(currentLight <= SensorManager.LIGHT_OVERCAST)
                        lightStr = "Cloudy";
                    else if(currentLight <= SensorManager.LIGHT_SUNLIGHT)
                        lightStr = "Overcast";
                    lightTextView.setText(lightStr);
                    lightTextView.invalidate();
                }
                if(!Float.isNaN(currentTemperture)){
                    temperatureTextView.setText(currentTemperture+"C");
                    temperatureTextView.invalidate();
                }
            }
        });
    }
}
