
package com.example.fxos.androidtips05;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class EditActivity extends Activity {
    public static final String AUTO_SHOW_FLAG = "AUTO_SHOW_FLAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        // keybord is shown
        if (getIntent().getBooleanExtra(AUTO_SHOW_FLAG, false)) {
            getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        // keybord is hidden
        else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }
}
