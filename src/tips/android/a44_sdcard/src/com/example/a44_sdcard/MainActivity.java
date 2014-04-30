
package com.example.a44_sdcard;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class MainActivity extends Activity {

    String mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(mState)) {
            Toast.makeText(getApplicationContext(), R.string.sd_true, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.sd_false, Toast.LENGTH_SHORT).show();
        }
    }

}
