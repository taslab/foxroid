package com.example.fxos.androidtips17;

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

	private MediaPlayer player;
	private Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	private ListView list;
	private TextView txtAudioLabel;
	private MySimpleCursorAdapter mAdapter;
	private Button btnPlayOrPause;
	private long currentPlayingSongId;
	Uri sampleMediaUri;

	private Handler handler = new Handler();
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        list = (ListView) findViewById(android.R.id.list);

        btnPlayOrPause = (Button) findViewById(R.id.btn_play_or_pause);

        //get file from ContentResolver
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(baseUri, null, null, null, null);
        
        currentPlayingSongId = 0;
        if(cursor.moveToFirst()) {
        	currentPlayingSongId = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        	
			//set playing music title
        	txtAudioLabel =  (TextView)findViewById(R.id.txt_audio_label);
        	txtAudioLabel.setText(getString(R.string.music_title_prefix ,(cursor.getPosition() + 1) + "." + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
        } else {
        	Toast.makeText(this, getString(R.string.message_file_not_found), Toast.LENGTH_SHORT).show();
        	finish();
        }
        mAdapter = new MySimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String [] {MediaStore.MediaColumns.TITLE}, new int [] {android.R.id.text1});
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				currentPlayingSongId = id;

				//set playing music title
				setMusicTitle(position);
				playAudio(currentPlayingSongId);
			}
		});

		//play first song
		playAudio(currentPlayingSongId);
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
					updatePausePlay();
				}
			});
			player.setOnSeekCompleteListener(new OnSeekCompleteListener() {
				@Override
				public void onSeekComplete(MediaPlayer mp) {
					if(existsNextMusic()) {
						playAudio(currentPlayingSongId);
						return;
					}
				}
			});
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(existsNextMusic()) {
						playAudio(currentPlayingSongId);
						return;
					}
					//update play button status
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							updatePausePlay();
						}
					}, 100);
		       }
			});

			player.setDataSource(this, sampleMediaUri);
			player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private boolean existsNextMusic() {
		int size = list.getAdapter().getCount();
		for(int i = 0; i < size; i++) {
			long checkId = list.getItemIdAtPosition(i);
			//play next music
			if(checkId == currentPlayingSongId && i + 1 < size) {
				list.setSelection(i+1);
				currentPlayingSongId = list.getItemIdAtPosition(i + 1);

				//set playing music title
				setMusicTitle(i+1);
				return true;
			}
		}
		return false;
	}
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
			switch(event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				finish();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	//set playing music title
	private void setMusicTitle(int position) {
		Cursor cursor = mAdapter.getCursor();
		cursor.moveToPosition(position);
    	txtAudioLabel.setText(getString(R.string.music_title_prefix ,(cursor.getPosition() + 1) + "." + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
	}

	//update button status
	private void updatePausePlay() {
		if(player != null && player.isPlaying()) {
			btnPlayOrPause.setBackgroundResource(android.R.drawable.ic_media_pause);
		} else {
			btnPlayOrPause.setBackgroundResource(android.R.drawable.ic_media_play);
		}
	}
    public void onClick(View view) {
    	switch(view.getId()) {
    	//play
    	case R.id.btn_play_or_pause:
    		if(player != null) {
    			if(player.isPlaying()) {
    				player.pause();
    			} else {
    				player.start();
    			}
    			updatePausePlay();
    		}
    		
    		break;
    	//restart
    	case R.id.btn_restart:
    		//restart from first position
    		list.setSelection(0);
			currentPlayingSongId = list.getItemIdAtPosition(0);

			//set playing music title
			setMusicTitle(0);
			playAudio(currentPlayingSongId);
    		break;
    	}
    }
    //stop music
    private void clearPlayer() {
		if(player != null) {
    		player.stop();
    		player.reset();
    		player.release();
    		player = null;
		}
    }
    class MySimpleCursorAdapter extends SimpleCursorAdapter {
    	private int layout;

		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
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
			label.setText((cursor.getPosition()+1) + "." + cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));
		}
    }
}
