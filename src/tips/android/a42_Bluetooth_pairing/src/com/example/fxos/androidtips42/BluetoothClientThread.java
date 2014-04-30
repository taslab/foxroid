
package com.example.fxos.androidtips42;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BluetoothClientThread extends Thread {
    private BluetoothSocket clientSocket = null;

    private final BluetoothDevice mDevice;

    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothClientThread(Context context, BluetoothDevice device, BluetoothAdapter btAdapter) {
        mContext = context;
        mDevice = device;
        mBluetoothAdapter = btAdapter;

        try {
            // get socket
            clientSocket = mDevice.createRfcommSocketToServiceRecord(Const.BT_SAMPLE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // cancel discovery before connect
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        try {
            // connect to Bluetooth server
            clientSocket.connect();
        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException closeException) {
                e.printStackTrace();
            }
            return;
        }

        // initialize stream
        StreamThread streamThread = new StreamThread(mContext, clientSocket);
        try {
            // send message to server
            String clientMessage = mContext.getResources().getString(R.string.message_client);
            streamThread.write(clientMessage.getBytes(Const.CHAR_SET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // get message from server
        streamThread.start();
    }

    public void cancel() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
