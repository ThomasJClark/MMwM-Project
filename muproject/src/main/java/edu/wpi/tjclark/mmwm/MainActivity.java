package edu.wpi.tjclark.mmwm;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private BluetoothManager bluetoothManager;
    private BluetoothHelper bluetoothHelper;
    private ControlMode controlMode;

    private Button cButton, dButton, eButton, fButton, gButton, connect;

    /**
     * Sends a single note using the {@link BluetoothHelper} when a button is
     * clicked.
     *
     * @see BluetoothHelper#sendNotes(int)
     */
    private class SendNote implements Button.OnClickListener {
        final int note;

        /**
         * @param note
         * @see BluetoothHelper#sendNotes(int)
         */
        public SendNote(int note) {
            this.note = note;
        }

        @Override
        public void onClick(View v) {
            MainActivity.this.bluetoothHelper.sendNotes(this.note);
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.cButton = (Button) findViewById(R.id.c1);
        this.cButton.setOnClickListener(new SendNote(BluetoothHelper.NOTE_C1));

        this.dButton = (Button) findViewById(R.id.d1);
        this.dButton.setOnClickListener(new SendNote(BluetoothHelper.NOTE_D1));

        this.eButton = (Button) findViewById(R.id.e1);
        this.eButton.setOnClickListener(new SendNote(BluetoothHelper.NOTE_E1));

        this.fButton = (Button) findViewById(R.id.f1);
        this.fButton.setOnClickListener(new SendNote(BluetoothHelper.NOTE_F1));

        this.gButton = (Button) findViewById(R.id.g1);
        this.gButton.setOnClickListener(new SendNote(BluetoothHelper.NOTE_G1));

        this.connect = (Button) findViewById(R.id.connect);
        this.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothHelper.isConnected()) {
                    Log.i(TAG, "lol already connected");
                } else {
                    bluetoothHelper.connect();
                }
            }
        });

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        this.bluetoothHelper = new BluetoothHelper(this, this.bluetoothManager);
        this.bluetoothHelper.connect();

        this.controlMode = new FlickAndSweepControlMode(this.bluetoothHelper);
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
