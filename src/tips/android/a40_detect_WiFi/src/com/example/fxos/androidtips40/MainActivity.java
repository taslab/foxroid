
package com.example.fxos.androidtips40;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnCheckedChangeListener {
    private TextView txtWifiState;

    private WifiManager mWifiManager;

    private WifiReceiver mWifiReceiver;

    private ToggleButton toggleWifiState;

    // Receiver for getting WiFi state
    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // get info at changing WiFi state
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int tmpWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);

                // disp WiFi state to screen
                switch (tmpWifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        txtWifiState.setText(getString(R.string.wifi_state_disabled));
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        txtWifiState.setText(getString(R.string.wifi_state_disabling));
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        txtWifiState.setText(getString(R.string.wifi_state_enabled));
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        txtWifiState.setText(getString(R.string.wifi_state_enabling));
                        break;
                    default:
                        txtWifiState.setText(getString(R.string.wifi_state_default));
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWifiState = (TextView)findViewById(R.id.txt_wifi_state);
        toggleWifiState = (ToggleButton)findViewById(R.id.toggle_wifi_state);

        mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        // init toggle button
        if (mWifiManager.isWifiEnabled()) {
            toggleWifiState.setChecked(true);
        }

        toggleWifiState.setOnCheckedChangeListener(this);
        mWifiReceiver = new WifiReceiver();
    }

    protected void onResume() {
        super.onResume();
        // regist receiver
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    protected void onPause() {
        super.onPause();
        // unregist receiver
        unregisterReceiver(mWifiReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // WiFi ON
        if (isChecked) {
            mWifiManager.setWifiEnabled(true);
            // WiFi OFF
        } else {
            mWifiManager.setWifiEnabled(false);
        }
    }
}
