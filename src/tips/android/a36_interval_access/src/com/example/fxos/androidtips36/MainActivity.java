
package com.example.fxos.androidtips36;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 0x10;

    private static final long INTERVAL = 5000;

    private Intent mIntent;

    private PendingIntent mOperation;

    private AlarmManager mAlarmManager;

    private TextView mTextResult;

    private OperationReceiver mOperationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextResult = (TextView)findViewById(R.id.textResult);

        // set AlarmManager
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // set service
        mIntent = new Intent(this, TimerService.class);
        mOperation = PendingIntent.getService(this, REQUEST_CODE, mIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // receiver to receive service result
        mOperationReceiver = new OperationReceiver();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonIntervalStart:
                // start repeat
                mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), INTERVAL,
                        mOperation);
                view.setEnabled(false);
                break;
            case R.id.buttonIntervalStop:
                // stop repeat
                mAlarmManager.cancel(mOperation);
                findViewById(R.id.buttonIntervalStart).setEnabled(true);
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(mOperationReceiver, new IntentFilter(TimerService.ACTION_REFLESH));
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(mOperationReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        mAlarmManager.cancel(mOperation);
    }

    class OperationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // set result value
            mTextResult.setText(intent.getStringExtra(TimerService.EXTRA_REFLESH_VALUE));
        }
    }
}
