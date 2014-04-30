
package com.example.fxos.androidtips42;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class StreamThread extends Thread {
    public static InputStream mInputStream;

    public static OutputStream mOutputStream;

    private Context mContext;

    public StreamThread(Context context, BluetoothSocket socket) {
        mContext = context;
        try {
            mInputStream = socket.getInputStream();
            mOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] buf) {
        try {
            // write data
            mOutputStream.write(buf);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] buf = new byte[1024];
        String receiveData = null;
        int tmpBuf = 0;

        while (true) {
            try {
                // read data
                tmpBuf = mInputStream.read(buf);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if (tmpBuf != 0) {
                try {
                    // make String
                    receiveData = new String(buf, 0, tmpBuf, Const.CHAR_SET);

                    // reflect message
                    Intent intent = new Intent(Const.ACTION_UPDATE_MESSAGE);
                    intent.putExtra(Const.EXTRA_UPDATE_MESSAGE, receiveData);
                    mContext.sendBroadcast(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
