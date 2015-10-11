package edu.wpi.tjclark.mmwm;

import android.bluetooth.*;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.UUID;

public class BluetoothHelper extends BluetoothGattCallback {

    private final static String TAG = BluetoothHelper.class.getSimpleName();

    private final static String RFDUINO_ADDRESS = "CB:74:26:E5:7A:EC";

    private final static UUID UUID_SERVICE =
            UUID.fromString("000002220-0000-1000-8000-00805F9B34FB");

    private final static UUID UUID_SEND =
            UUID.fromString("000002222-0000-1000-8000-00805F9B34FB");

    public final static int NOTE_C1 = 0x01;
    public final static int NOTE_D1 = 0x02;
    public final static int NOTE_E1 = 0x04;
    public final static int NOTE_F1 = 0x08;
    public final static int NOTE_G1 = 0x10;
    public final static int NOTE_A1 = 0x20;
    public final static int NOTE_B1 = 0x40;
    public final static int NOTE_C2 = 0x80;

    private final BluetoothManager bluetoothManager;
    private final Context context;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic send;
    private boolean connected = false;

    public BluetoothHelper(Context context, BluetoothManager bluetoothManager) {
        this.context = context;
        this.bluetoothManager = bluetoothManager;
    }

    /**
     * Start connecting to the bluetooth device.
     */
    public void connect() {
        Log.i(TAG, "step 1: starting scan...");

        final BluetoothAdapter adapter = this.bluetoothManager.getAdapter();
        adapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device.getAddress().equals(RFDUINO_ADDRESS)) {
                    Log.i(TAG, "step 2: found device! connecting...");
                    adapter.stopLeScan(this);
                    device.connectGatt(context, true, BluetoothHelper.this);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param gatt
     * @param status
     * @param state
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int state) {
        if (state != BluetoothGatt.STATE_CONNECTED) {
            Log.e(TAG, "not connected");
            this.connected = false;
            return;
        }

        Log.i(TAG, "step 3: discovering services...");
        this.bluetoothGatt = gatt;
        this.bluetoothGatt.discoverServices();
    }

    /**
     * {@inheritDoc}
     *
     * @param gatt
     * @param status
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.e(TAG, "service discovery was unsuccessful");
            return;
        }

        Log.i(TAG, "step 4: getting \"send\" characteristic...");
        this.send = this.bluetoothGatt.getService(UUID_SERVICE)
                .getCharacteristic(UUID_SEND);
        this.connected = true;
        Log.i(TAG, "connected!");
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic send,
                                      int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "characteristic written successfully");
        } else {
            Log.e(TAG, "error writing characteristic");
            Log.e(TAG, Arrays.toString(send.getValue()));
        }
    }


    public boolean isConnected() {
        return this.connected;
    }

    /**
     * @param notes A bitfield of the notes that should play
     * @see BluetoothHelper#NOTE_C1
     * @see BluetoothHelper#NOTE_D1
     * @see BluetoothHelper#NOTE_E1
     * @see BluetoothHelper#NOTE_F1
     * @see BluetoothHelper#NOTE_G1
     * @see BluetoothHelper#NOTE_A1
     * @see BluetoothHelper#NOTE_B1
     * @see BluetoothHelper#NOTE_C2
     */
    public void sendNotes(int notes) {
        if (!this.connected) {
            Log.e(TAG, "sending note while not connected!");
            return;
        }

        this.send.setValue(new byte[]{(byte) notes});

        if (!this.bluetoothGatt.writeCharacteristic(this.send)) {
            Log.i(TAG, "sent notes");
        } else {
            Log.e(TAG, "lol it didn't work");
        }
    }
}
