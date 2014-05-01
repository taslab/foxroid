
package com.example.fxos.androidtips49;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_MESSAGE = "message";

    public static final String PROPERTY_REG_ID = "registration_id";

    private static final String PROPERTY_APP_VERSION = "appVersion";

    // please change sender id if you build this app
    private static final String SENDER_ID = "591404333141";

    private Context mContext;

    private GoogleCloudMessaging mGcm;

    private TextView txtDisplay;

    private EditText editSendMessage;

    private NotificationManager mNotificationManager;

    private String mRegid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        txtDisplay = (TextView)findViewById(R.id.txt_display);
        editSendMessage = (EditText)findViewById(R.id.edit_send_message);

        mContext = getApplicationContext();
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            mGcm = GoogleCloudMessaging.getInstance(this);
            mRegid = getRegistrationId(this);

            if (mRegid.isEmpty()) {
                registerInBackground();
            } else {
                txtDisplay.setText(getString(R.string.reg_id_prefix) + mRegid);

            }
        } else {
            Toast.makeText(this, R.string.message_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // you need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        // notification will cancel if it is already shown
        mNotificationManager.cancel(GcmIntentService.NOTIFICATION_ID);
    }

    private boolean checkPlayServices() {
        // check service is abailable
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            // service is not available but user can deal with this error
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, R.string.message_user_cannot_deal_error, Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String message = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mRegid = mGcm.register(SENDER_ID);
                    message = getString(R.string.reg_id_prefix) + mRegid;

                    // you can define the processing to send an Registration ID
                    // to the server here

                    // store Registration ID
                    storeRegistrationId(mContext, mRegid);
                } catch (IOException ex) {
                    message = "Error :" + ex.getMessage();
                }
                return message;
            }

            @Override
            protected void onPostExecute(String message) {
                txtDisplay.append(message);
            }

        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public void onClick(final View view) {
        if (view == findViewById(R.id.btn_send_message)) {
            new AsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    // send POST instead of server
                    PostUtil.sendMessage(getApplicationContext(), mRegid, editSendMessage.getText()
                            + "");
                    return null;
                }

                @Override
                protected void onPostExecute(String msg) {
                    // clear input
                    editSendMessage.setText("");
                }

            }.execute(null, null, null);
        }
    }

}
