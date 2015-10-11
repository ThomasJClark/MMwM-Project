package edu.wpi.tjclark.mmwm;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;


public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private BluetoothManager bluetoothManager;
    private BluetoothHelper bluetoothHelper;
    private ControlMode controlMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        this.bluetoothHelper = new BluetoothHelper(this, this.bluetoothManager);
        this.bluetoothHelper.connect();

        this.controlMode = new FlickAndSweepControlMode();
    }

    /**
     * Set a new control mode.  The control mode determines how sensor readings
     * map to musical notes.
     *
     * @param controlMode
     */
    private void changeControlMode(ControlMode controlMode) {
        this.unregisterControlMode();
        this.controlMode = controlMode;
        this.registerControlMode();
    }

    /**
     * Unregister the current {@link ControlMode} so it will no longer receive
     * sensor events.
     */
    private void unregisterControlMode() {
        if (controlMode == null) {
            return;
        }

        this.sensorManager.unregisterListener(this.controlMode);
    }

    /**
     * Register the current {@link ControlMode} to receive sensor events.
     */
    private void registerControlMode() {
        this.sensorManager.registerListener(this.controlMode,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 100);

        this.sensorManager.registerListener(this.controlMode,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);
    }


    @Override
    public void onResume() {
        super.onResume();

        this.registerControlMode();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.unregisterControlMode();
    }
}
