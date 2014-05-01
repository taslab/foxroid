
package com.example.fxos.androidtips49;

import android.content.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PostUtil {
    static final String SEND_URL = "https://android.googleapis.com/gcm/send";

    // please change api key if you build this app
    private static String API_KEY = "AIzaSyDLI9snubpKKmyhFCK4iOiWD9XN3XO6UD4";

    private static String PARAM_REGISTRATION_ID = "registration_id";

    private static String PARAM_COLLAPSE_KEY = "collapse_key";

    private static String PARAM_DATA_MESSAGE = "data.message";

    private static String PARAM_AUTHORIZATION = "Authorization";

    private static String AUTHORIZATION_PREFIX = "key=";

    private static final String CHAR_SET = "UTF-8";

    private static final String METHOD_TYPE = "POST";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_PARAM = "application/x-www-form-urlencoded;charset=UTF-8";

    public static boolean sendMessage(Context context, String regId, String msg) {
        try {
            // encode UTF-8
            msg = URLEncoder.encode(msg, CHAR_SET);

            // set body
            StringBuilder bodyBuffer = new StringBuilder();
            bodyBuffer.append(PARAM_REGISTRATION_ID).append("=").append(regId).append("&")
                    .append(PARAM_COLLAPSE_KEY).append("=").append("1").append("&")
                    .append(PARAM_DATA_MESSAGE).append("=").append(msg);

            // set header
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(PARAM_AUTHORIZATION, AUTHORIZATION_PREFIX + API_KEY);

            post(context, SEND_URL, bodyBuffer.toString(), headers);
            return true;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void post(Context context, String endpoint, String bodyString,
            Map<String, String> headers) throws IOException {

        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(endpoint);

            byte[] bytes = bodyString.getBytes();
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod(METHOD_TYPE);
            conn.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_PARAM);
            Iterator<Entry<String, String>> iteratorHeaders = headers.entrySet().iterator();
            while (iteratorHeaders.hasNext()) {
                Entry<String, String> header = iteratorHeaders.next();
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();

            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException(context.getString(R.string.message_post_error) + status);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(endpoint);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
