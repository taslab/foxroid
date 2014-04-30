
package com.example.fxos.androidtips42;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;

public class BluetoothServerThread extends Thread {
    private BluetoothServerSocket serverSocket = null;

    private BluetoothAdapter mBluetoothAdapter;

    private Context mContext;

    public BluetoothServerThread(Context context, BluetoothAdapter btAdapter) {
        mContext = context;
        mBluetoothAdapter = btAdapter;
        try {
            // get server socket
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("",
                    Const.BT_SAMPLE_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        BluetoothSocket connectedSocket = null;
        try {
            // get socket
            connectedSocket = serverSocket.accept();

            // initialize stream
            StreamThread streamThread = new StreamThread(mContext, connectedSocket);

            // send message to client
            String serverMessage = mContext.getResources().getString(R.string.message_server);
            streamThread.write(serverMessage.getBytes(Const.CHAR_SET));

            // get message from client
            streamThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
