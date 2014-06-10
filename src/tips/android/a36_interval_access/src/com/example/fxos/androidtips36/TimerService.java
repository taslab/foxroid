
package com.example.fxos.androidtips36;

import android.app.IntentService;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TimerService extends IntentService {
    private static final String TAG = TimerService.class.getSimpleName();

    public static final String ACTION_REFLESH = "com.example.intent.REFLESH";

    public static final String EXTRA_REFLESH_VALUE = "com.example.extra.REFLESH_VALUE";

    private static final String INTERVAL_ACCESS_SITE = "http://ntp-a1.nict.go.jp/cgi-bin/time";

    public TimerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            // connect interval access site
            URL url = new URL(INTERVAL_ACCESS_SITE);
            connection = (HttpURLConnection)url.openConnection();
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    // get result
                    is = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1000];
                    int size = 0;
                    while ((size = is.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, size);
                    }

                    // convert to string
                    String result = new String(bos.toByteArray(), 0, bos.size(), "UTF-8");

                    // send broadcast
                    Intent resultIntent = new Intent(ACTION_REFLESH);
                    resultIntent.putExtra(EXTRA_REFLESH_VALUE, result);
                    sendBroadcast(resultIntent);

                    break;
                default:
                    android.util.Log.d("MainActivity", "access failure.");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
