
package com.example.fxos.androidtips52;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    private static final int NOTIFY_ID = 0x10;

    private MediaPlayer mPlayer;

    private Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private ListView list;

    private TextView txtAudioLabel;

    private MySimpleCursorAdapter mAdapter;

    private Button btnPlayOrPause;

    private boolean finishFlag = false;

    private long currentPlayingSongId;

    private NotificationManager mNotificationManager;

    Uri sampleMediaUri;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = getListView();

        // set NotificationManager
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        btnPlayOrPause = (Button)findViewById(R.id.btn_play_or_pause);

        // get file from ContentResolver
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(baseUri, null, null, null, null);

        currentPlayingSongId = 0;
        if (cursor.moveToFirst()) {
            currentPlayingSongId = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));

            // set playing music title
            txtAudioLabel = (TextView)findViewById(R.id.txt_audio_label);
            txtAudioLabel
                    .setText(getString(
                            R.string.music_title_prefix,
                            (cursor.getPosition() + 1)
                                    + "."
                                    + cursor.getString(cursor
                                            .getColumnIndex(MediaStore.MediaColumns.TITLE))));
        } else {
            Toast.makeText(this, getString(R.string.message_file_not_found), Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
        mAdapter = new MySimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                new String[] {
                    MediaStore.MediaColumns.TITLE
                }, new int[] {
                    android.R.id.text1
                });

        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                currentPlayingSongId = id;

                // set playing music title
                setMusicTitle(position);
                playAudio(currentPlayingSongId);
            }
        });

        // play first song
        playAudio(currentPlayingSongId);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer.isPlaying() && !finishFlag) {
            // initialize Notification
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // set builder
            Notification.Builder builder = new Notification.Builder(this);

            builder.setSmallIcon(R.drawable.ic_launcher).setTicker(null)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Touch for return.").setContentIntent(contentIntent)
                    .setWhen(System.currentTimeMillis());

            // generate notification
            Notification notification = builder.getNotification();
            notification.flags = Notification.FLAG_ONGOING_EVENT;

            // notify
            mNotificationManager.notify(NOTIFY_ID, notification);
        }
    }

    protected void onResume() {
        super.onResume();
        finishFlag = false;
        mNotificationManager.cancel(NOTIFY_ID);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPlayer();
    }

    private void playAudio(long id) {
        sampleMediaUri = Uri.withAppendedPath(baseUri, "" + id);

        clearPlayer();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setLooping(false);
            mPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                    mPlayer.setLooping(false);
                    updatePausePlay();
                }
            });
            mPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    if (existsNextMusic()) {
                        playAudio(currentPlayingSongId);
                        return;
                    }
                }
            });
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (existsNextMusic()) {
                        playAudio(currentPlayingSongId);
                        return;
                    }
                    // update play button status
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updatePausePlay();
                        }
                    }, 100);
                }
            });

            mPlayer.setDataSource(this, sampleMediaUri);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean existsNextMusic() {
        int size = list.getAdapter().getCount();
        for (int i = 0; i < size; i++) {
            long checkId = list.getItemIdAtPosition(i);
            // play next music
            if (checkId == currentPlayingSongId && i + 1 < size) {
                list.setSelection(i + 1);
                currentPlayingSongId = list.getItemIdAtPosition(i + 1);

                // set playing music title
                setMusicTitle(i + 1);
                return true;
            }
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    finishFlag = true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    // set playing music title
    private void setMusicTitle(int position) {
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        txtAudioLabel.setText(getString(R.string.music_title_prefix, (cursor.getPosition() + 1)
                + "." + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
    }

    // update button status
    private void updatePausePlay() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            btnPlayOrPause.setBackgroundResource(android.R.drawable.ic_media_pause);
        } else {
            btnPlayOrPause.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
        // play
            case R.id.btn_play_or_pause:
                if (mPlayer != null) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.pause();
                    } else {
                        mPlayer.start();
                    }
                    updatePausePlay();
                }
                break;
            // restart
            case R.id.btn_restart:
                // restart from first position
                list.setSelection(0);
                currentPlayingSongId = list.getItemIdAtPosition(0);

                // set playing music title
                setMusicTitle(0);
                playAudio(currentPlayingSongId);
                break;
        }
    }

    // stop music
    private void clearPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    class MySimpleCursorAdapter extends SimpleCursorAdapter {
        private int layout;

        public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.layout = layout;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup arg2) {
            final View view = LayoutInflater.from(context).inflate(layout, null);

            ViewHolder holder = new ViewHolder();
            holder.label = (TextView)view.findViewById(android.R.id.text1);
            view.setTag(holder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder)view.getTag();
            holder.label.setText((cursor.getPosition() + 1) + "."
                    + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));
        }

        class ViewHolder {
            TextView label;
        }
    }
}
