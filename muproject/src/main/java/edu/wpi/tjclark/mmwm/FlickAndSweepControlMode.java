package edu.wpi.tjclark.mmwm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by tom on 10/1/15.
 */
public class FlickAndSweepControlMode implements ControlMode {

    private static String TAG = FlickAndSweepControlMode.class.getSimpleName();

    /**
     * The minimum acceleration needed to be considered a "flick"
     */
    private static final float FLICK_THRESHOLD = 15.0f;

    /**
     * The time after a flick where no other high acceleration readings are
     * considered flicks.  This is to prevent a single movement from being
     * interpreted as a new flick at every sample.
     * <p/>
     * The value of 200ms was chosen by trial and error as the corresponding
     * frequency (5 Hz) is faster than a human would reasonably be able to
     * flick a phone, yet 200ms is also longer than a human would sustain an
     * acceleration above the flick threshold.
     */
    private static final long FLICK_TIMEOUT = 200l;

    private long lastFlickTime = -1;
    private float azimuth = 0.0f;
    private int currentNote = 0;

    private final BluetoothHelper bluetoothHelper;

    public FlickAndSweepControlMode(BluetoothHelper bluetoothHelper) {
        this.bluetoothHelper = bluetoothHelper;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                float azimuth = event.values[0], pitch = event.values[1], roll = event.values[2];

                this.azimuth = azimuth;
                if (azimuth > 360.0f) {
                    this.currentNote = 0;
                } else if (azimuth > 300.0f) {
                    this.currentNote = BluetoothHelper.NOTE_G1;
                } else if (azimuth > 240.0f) {
                    this.currentNote = BluetoothHelper.NOTE_F1;
                } else if (azimuth > 180.0f) {
                    this.currentNote = BluetoothHelper.NOTE_E1;
                } else if (azimuth > 120.0f) {
                    this.currentNote = BluetoothHelper.NOTE_D1;
                } else if (azimuth > 60.0f) {
                    this.currentNote = BluetoothHelper.NOTE_C1;
                } else {
                    this.currentNote = 0;
                }

                break;

            case Sensor.TYPE_ACCELEROMETER:
                // Try to detect flicks based on spikes in acceleration
                if (System.currentTimeMillis() > lastFlickTime + FLICK_TIMEOUT) {
                    final float flickMagnitude =
                            getAccelerationMagnitude(event.values);

                    if (flickMagnitude >= FLICK_THRESHOLD) {
                        this.lastFlickTime = System.currentTimeMillis();

                        Log.i(TAG, "" + this.currentNote + " <-> " + this.azimuth);
                        this.bluetoothHelper.sendNotes(this.currentNote);
                    }
                }
        }
    }

    /**
     * Given the latest accelerometer reading, return the magnitude of the
     * phone's acceleration, taking into account gravity.
     */
    private float getAccelerationMagnitude(float[] accelerometerValues) {
        final float x = accelerometerValues[0];
        final float y = accelerometerValues[1];
        final float z = accelerometerValues[2];
        final float accelerationSquared = x * x + y * y + z * z;

        return (float) Math.sqrt(accelerationSquared -
                SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
