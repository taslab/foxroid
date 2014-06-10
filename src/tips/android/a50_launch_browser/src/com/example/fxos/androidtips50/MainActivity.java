
package com.example.fxos.androidtips50;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    private EditText mEditInputUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditInputUrl = (EditText)findViewById(R.id.editInputUrl);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLaunchBrowser:
                try {
                    // start intent
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse(mEditInputUrl.getText() + ""));
                    startActivity(intent);
                } catch (Exception e) {
                    // show error message
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(R.string.message_launch_failed)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.message_error_title)
                            .setPositiveButton(R.string.button_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();

                    e.printStackTrace();
                }
                break;
        }
    }
}
