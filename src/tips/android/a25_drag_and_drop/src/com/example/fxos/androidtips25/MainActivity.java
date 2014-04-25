
package com.example.fxos.androidtips25;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnDragListener {
    private MediaPlayer player;

    private Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private ListView list;

    private TextView txtLabel;

    private MySimpleCursorAdapter mAdapter;

    private String musicId;

    private float lastDragX;

    private float lastDragY;

    Uri sampleMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(android.R.id.list);

        // set drag listener
        findViewById(R.id.layout_base).setOnDragListener(this);

        // get file from ContentResolver
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(baseUri, null, null, null, null);

        if (cursor.moveToFirst()) {
            // set label
            txtLabel = (TextView)findViewById(R.id.txt_label);
            txtLabel.setText(R.string.long_push_message);
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

                // set drag label
                txtLabel.setText(R.string.drop_message);

                // currentPlayingSongId = id;
                ClipData data = ClipData.newPlainText("id", "" + id);

                view.startDrag(data, new DragShadowBuilder(view), view, 0);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPlayer();
    }

    private void playAudio(String id) {
        sampleMediaUri = Uri.withAppendedPath(baseUri, id);

        clearPlayer();
        player = new MediaPlayer();
        try {
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
            return LayoutInflater.from(context).inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView label = (TextView)view.findViewById(android.R.id.text1);
            // set background
            label.setBackgroundResource(R.drawable.list_background);

            // set text color
            Resources r = getResources();
            XmlResourceParser parser = r.getXml(R.drawable.list_font);
            try {
                ColorStateList csl = ColorStateList.createFromXml(r, parser);
                label.setTextColor(csl);
            } catch (IOException eIO) {
            } catch (XmlPullParserException eXPP) {
            }

            // set label
            label.setText((cursor.getPosition() + 1) + "."
                    + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                // save drop position
                lastDragX = event.getX();
                lastDragY = event.getY();

                // set id from Clipdata
                Item data = event.getClipData().getItemAt(0);
                musicId = (String)data.getText();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                float textX = txtLabel.getX();
                float textY = txtLabel.getY();

                // change normal label
                txtLabel.setText(R.string.long_push_message);

                if (Math.abs(lastDragX - textX) <= 200 && Math.abs(lastDragY - textY) <= 100) {
                    // play music
                    playAudio(musicId);
                }
                break;
            case DragEvent.ACTION_DRAG_STARTED:
            case DragEvent.ACTION_DRAG_LOCATION:
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_EXITED:
            default:
                break;

        }
        return true;
    }
}
