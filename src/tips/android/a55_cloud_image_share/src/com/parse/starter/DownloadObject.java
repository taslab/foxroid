package com.parse.starter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Coty-Saxman on 2014/08/18.
 */
public class DownloadObject {
    private String urlString;
    private ImageView destIv;
    private Bitmap bm;

    public DownloadObject(String url, ImageView iv) {
        urlString = url;
        destIv = iv;
    }

    /**Returns a URL based on the String object passed in the constructor.*/
    public URL getUrl() {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**Sets the bitmap.*/
    public void setBitmap(Bitmap b) {
        bm = b;
    }

    public void placeBitmap() {
        destIv.setImageBitmap(bm);
    }
}
