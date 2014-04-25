
package com.example.fxos.androidtips26;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    private static final int WHICH_DETAIL = 0;

    private static final int WHICH_PLAY = 1;

    private MediaPlayer player;

    private Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private ListView list;

    private TextView txtAudioLabel;

    private MySimpleCursorAdapter mAdapter;

    private long currentPlayingSongId;

    Uri sampleMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(android.R.id.list);

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
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, final int position,
                    final long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(R.string.title_choose_dialog)
                        .setItems(R.array.choose_menu, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                // show detail info
                                    case WHICH_DETAIL:
                                        Cursor cursor = mAdapter.getCursor();
                                        cursor.moveToPosition(position);

                                        // set date format
                                        SimpleDateFormat format = new SimpleDateFormat(
                                                "yyyy-MM-dd HH:mm:ss", new Locale("ja"));
                                        Date addedDate = new Date(
                                                cursor.getLong(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)) * 1000);
                                        Date modifiedDate = new Date(
                                                cursor.getLong(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)) * 1000);

                                        // set detail info
                                        StringBuffer buffer = new StringBuffer();
                                        buffer.append("title:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.TITLE)))
                                                .append("\n")
                                                .append("id:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns._ID)))
                                                .append("\n")
                                                .append("data:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.DATA)))
                                                .append("\n")
                                                .append("display name:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)))
                                                .append("\n")
                                                .append("date added:")
                                                .append(format.format(addedDate))
                                                .append("\n")
                                                .append("date modified:")
                                                .append(format.format(modifiedDate))
                                                .append("\n")
                                                .append("MIME type:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)))
                                                .append("\n")
                                                .append("size:")
                                                .append(cursor.getString(cursor
                                                        .getColumnIndex(MediaStore.MediaColumns.SIZE)));

                                        // show dialog
                                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(
                                                MainActivity.this);
                                        detailDialog
                                                .setTitle(R.string.title_detail_info)
                                                .setMessage(buffer.toString())
                                                .setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                            }
                                                        }).show();
                                        break;
                                    // play music
                                    case WHICH_PLAY:
                                        currentPlayingSongId = id;

                                        // set playing music title
                                        setMusicTitle(position);
                                        playAudio(currentPlayingSongId);
                                        break;
                                }
                            }
                        }).show();
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPlayer();
    }

    private void playAudio(long id) {
        sampleMediaUri = Uri.withAppendedPath(baseUri, "" + id);

        clearPlayer();
        player = new MediaPlayer();
        try {
            player.setLooping(false);
            player.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    player.setLooping(false);
                }
            });
            player.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    player.stop();
                }
            });

            player.setDataSource(this, sampleMediaUri);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // set playing music title
    private void setMusicTitle(int position) {
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        txtAudioLabel.setText(getString(R.string.music_title_prefix, (cursor.getPosition() + 1)
                + "." + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
    }

    // stop music
    private void clearPlayer() {
        if (player != null) {
            player.stop();
            player.reset();
            player.release();
            player = null;
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
