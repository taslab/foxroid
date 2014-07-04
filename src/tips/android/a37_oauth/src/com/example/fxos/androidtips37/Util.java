
package com.example.fxos.androidtips37;

import android.content.Context;
import android.net.Uri;

public class Util {
    // This is sample value. Please change the values ​​to suit your
    // application.
    public static final String CONSUMER_KEY = "ho8BBUcFq7iaQrbv8scFg6JxF";

    // This is sample value. Please change the values ​​to suit your
    // application.
    public static final String SECRET_KEY = "WRVpAUODTG8qcfhKcd0SW8pknLX6hm7c2dKqmVYFtHa90sq4n2";

    public static final String VERIFIER = "oauth_verifier";

    // make callback url
    public static String getCallbackUrl(Context context) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(context.getString(R.string.callback_scheme)).authority(
                CallBackActivity.class.getSimpleName());

        return builder.toString();
    }

}
