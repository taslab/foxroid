package com.parse.starter;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Coty-Saxman on 2014/08/15.
 */
public class DownloadImageTask extends AsyncTask<DownloadObject, Void, DownloadObject> {
    protected DownloadObject doInBackground(DownloadObject... dlo) {
        try {
            URL myURL = dlo[0].getUrl();
            HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            dlo[0].setBitmap(BitmapFactory.decodeStream(is));
            return dlo[0];
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(DownloadObject dlo) {
        dlo.placeBitmap();
    }
}
