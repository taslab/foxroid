
package com.example.fxos.androidtips54;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    private static final String FIREFOX_PACKAGE = "org.mozilla.firefox";

    private static final Uri MARKET_URI = Uri.parse("market://details?id=" + FIREFOX_PACKAGE);

    PackageManager mPackageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPackageManager = getPackageManager();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLaunchOrMarket:
                // set launch intent.
                Intent intent = mPackageManager.getLaunchIntentForPackage(FIREFOX_PACKAGE);

                // set start market intent if application is not installed.
                if (intent == null) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(MARKET_URI);
                }
                startActivity(intent);
                break;
        }
    }

}
