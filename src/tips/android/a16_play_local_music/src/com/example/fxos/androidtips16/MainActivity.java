package com.example.fxos.androidtips16;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MediaPlayer player;
	Uri sampleMediaUri;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get file from ContentResolver
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()) {
        	//generate URI
        	int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
        	sampleMediaUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);

        	//set music title
        	TextView txtAudioLabel =  (TextView)findViewById(R.id.txt_audio_label);
        	txtAudioLabel.setText(getString(R.string.music_title_prefix, cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
        } else {
        	Toast.makeText(this, getString(R.string.message_file_not_found), Toast.LENGTH_SHORT).show();
        	finish();
        }
        
    }
	@Override
	protected void onPause() {
		super.onPause();
		clearPlayer();
	}
    public void onClick(View view) {
    	switch(view.getId()) {
    	//play
    	case R.id.btn_play:
    		try {
    			if(player == null) {
    				//set info
    	            player = new MediaPlayer();
    	            
    	            player.setDataSource(this, sampleMediaUri);
    				
    				player.setLooping(false);
    				player.prepare();
    				player.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
				    		clearPlayer();
						}
					});
    				//start play
    	    		player.start();
    			}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
}
