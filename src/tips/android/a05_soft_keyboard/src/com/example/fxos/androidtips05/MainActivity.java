
package com.example.fxos.androidtips05;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, EditActivity.class);

        switch (view.getId()) {
        // auto
            case R.id.btn_auto:
                intent.putExtra(EditActivity.AUTO_SHOW_FLAG, true);
                break;
            // manual
            case R.id.btn_manual:
                break;
        }
        startActivity(intent);
    }
}
