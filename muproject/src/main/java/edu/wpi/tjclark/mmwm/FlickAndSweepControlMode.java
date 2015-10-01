package edu.wpi.tjclark.mmwm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by tom on 10/1/15.
 */
public class FlickAndSweepControlMode implements ControlMode {

    /**
     * The minimum acceleration needed to be considered a "flick"
     */
    private static final float FLICK_THRESHOLD = 15.0f;

    /**
     * The time after a flick where no other high acceleration readings are considered flicks.  This is to prevent
     * a single movement from being interpreted as a new flick at every sample.
     * <p/>
     * The value of 200ms was chosen by trial and error as the corresponding frequency (5 Hz) is faster than a human
     * would reasonably be able to flick a phone, yet 200ms is also longer than a human would sustain an acceleration
     * above the flick threshold.
     */
    private static final long FLICK_TIMEOUT = 200l;

    private long lastFlickTime = -1;

    /**
     * {@inheritDoc
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                // TODO: convert this into a musical pitch
                float azimuth = event.values[0], pitch = event.values[1], roll = event.values[2];
//                System.out.println(azimuth + ", " + pitch + ", " + roll);

                break;

            case Sensor.TYPE_ACCELEROMETER:
                // Try to detect flicks based on spikes in acceleration
                if (System.currentTimeMillis() > lastFlickTime + FLICK_TIMEOUT) {
                    final float flickMagnitude = getAccelerationMagnitude(event.values);

                    if (flickMagnitude >= FLICK_THRESHOLD) {
                        this.lastFlickTime = System.currentTimeMillis();

                        // TODO: play note
                    }
                }
        }
    }

    /**
     * Given the latest accelerometer reading, return the magnitude of the phone's acceleration, taking into account
     * gravity.
     */
    private float getAccelerationMagnitude(float[] accelerometerValues) {
        final float x = accelerometerValues[0];
        final float y = accelerometerValues[1];
        final float z = accelerometerValues[2];
        final float accelerationSquared = x * x + y * y + z * z;

        return (float) Math.sqrt(accelerationSquared - SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
