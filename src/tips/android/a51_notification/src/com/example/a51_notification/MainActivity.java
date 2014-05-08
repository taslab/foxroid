
package com.example.a51_notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button mStartbtn;

    private static final String NOTIFICATION = "notification";

    public static final int NOTIFICATION_ID = 1;

    private NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getStringExtra(NOTIFICATION) != null) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }

        mStartbtn = (Button)findViewById(R.id.start);
        mStartbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startNotify();
            }
        });
    }

    private void startNotify() {
        NotificationManager mNotificationManager = (NotificationManager)this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NOTIFICATION, getResources().getString(R.string.comment));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.comment)).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
