package edu.wpi.tjclark.mmwm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView azimuthView;
    private TextView pitchView;
    private TextView rollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.azimuthView = (TextView) findViewById(R.id.azimuth);
        this.pitchView = (TextView) findViewById(R.id.pitch);
        this.rollView = (TextView) findViewById(R.id.roll);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                float azimuth = event.values[0], pitch = event.values[1], roll = event.values[2];

                this.azimuthView.setText(Float.toString(azimuth));
                this.pitchView.setText(Float.toString(pitch));
                this.rollView.setText(Float.toString(roll));

                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
