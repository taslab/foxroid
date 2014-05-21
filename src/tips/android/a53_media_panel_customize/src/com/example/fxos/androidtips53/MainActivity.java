
package com.example.fxos.androidtips53;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements MediaPlayerControl {

    private static final int SHOW_CONTROLLER_TIME_OUT = 1000 * 60 * 3;

    private MediaPlayer mPlayer;

    private MediaController mMediaController;

    private Uri baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private ViewGroup mBaseLayout;

    private TextView mTxtAudioLabel;

    private ImageView mRew;

    private ImageView mFfwd;

    private ImageView mPauseDummy;

    private ImageButton mPause;

    private SeekBar mSeekBar;

    private TextView mStartTime;

    private TextView mEndTime;

    private Handler mHandler = new Handler();

    private float mControllerTopMargin;

    private boolean customizeFlag = false;

    Uri sampleMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get file from ContentResolver
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(baseUri, null, null, null, null);

        if (cursor.moveToFirst()) {
            long playingSongId = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

            playAudio(playingSongId);
            // set playing music title
            mTxtAudioLabel = (TextView)findViewById(R.id.txt_audio_label);
            mTxtAudioLabel.setText(getString(R.string.music_title_prefix,
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE))));
            mTxtAudioLabel.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mMediaController != null && !mMediaController.isShowing()) {
                        showControllerPanel();
                    }
                    return false;
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.message_file_not_found), Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
        cursor.close();

        mMediaController = new MediaController(this);
        mMediaController.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showControllerPanel();
                return true;
            }
        });

        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(mTxtAudioLabel);
        mBaseLayout = (ViewGroup)mMediaController.getChildAt(0);
        // clear because in some cases the previous state remains
        mBaseLayout.setBackgroundColor(Color.TRANSPARENT);

        // set controller margin
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mControllerTopMargin = (int)getResources().getDimension(R.dimen.margin_controller_top);
        params.topMargin = (int)mControllerTopMargin;
        mBaseLayout.setLayoutParams(params);

        ViewGroup controllView = (ViewGroup)mBaseLayout.getChildAt(0);
        ViewGroup statusView = (ViewGroup)mBaseLayout.getChildAt(1);

        mRew = (ImageView)controllView.getChildAt(1);
        mPause = (ImageButton)controllView.getChildAt(2);
        mFfwd = (ImageView)controllView.getChildAt(3);

        mRew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // override for customize
                int pos = mPlayer.getCurrentPosition();
                pos -= 5000; // milliseconds
                mPlayer.seekTo(pos);

                showControllerPanel();
            }
        });
        mPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPauseResume();

                showControllerPanel();
            }
        });
        mFfwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // override for customize
                int pos = mPlayer.getCurrentPosition();
                pos += 15000; // milliseconds
                mPlayer.seekTo(pos);

                showControllerPanel();
            }
        });

        mStartTime = (TextView)statusView.getChildAt(0);
        mSeekBar = (SeekBar)statusView.getChildAt(1);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showControllerPanel();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long duration = mPlayer.getDuration();
                    long newposition = (duration * progress) / 1000L;
                    mPlayer.seekTo((int)newposition);
                }
            }
        });
        mEndTime = (TextView)statusView.getChildAt(2);
        mEndTime.setFocusable(false);
        mEndTime.setFocusableInTouchMode(false);

        // set dummy item
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                makeDummyButton();
            }

        }, 300);

    }

    private void showControllerPanel() {
        if (customizeFlag) {
            mPauseDummy.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.INVISIBLE);
        }
        mMediaController.show(SHOW_CONTROLLER_TIME_OUT);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                updatePausePlay();
            }
        }, 50);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearPlayer();
        finish();
    }

    /**
     * generate dummy button. MediaController.show() calls
     * MediaController.updatePausePlay(). updatePausePlay set standard image for
     * play and pause button. It overrides customize image. DummyButton does
     * keep customize status.
     */
    private void makeDummyButton() {
        if (mPauseDummy == null) {
            // get DisplayMetrics
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            mPauseDummy = new ImageView(this);
            mPauseDummy.setImageResource(R.drawable.play);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int topMargin = ((int)(getResources().getDimension(R.dimen.margin_dummy_top) + mControllerTopMargin));
            android.util.Log.d("MainActivity", "topMargin:" + topMargin + " density:"
                    + displayMetrics.density);

            params.topMargin = topMargin;
            params.leftMargin = 0;
            // adjust margin
            if (displayMetrics.density == 3.0f) {
                params.topMargin--;
                params.leftMargin++;
            } else if (displayMetrics.density == 2.0f) {
                params.leftMargin++;
            }

            params.gravity = Gravity.CENTER_HORIZONTAL;
            mMediaController.addView(mPauseDummy, params);

            // set invisible first
            mPauseDummy.setVisibility(View.INVISIBLE);
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }

    private void updatePausePlay() {
        mPause.setVisibility(View.VISIBLE);

        if (mPause == null || !customizeFlag) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPause.setImageResource(R.drawable.pause);
        } else {
            mPause.setImageResource(R.drawable.play);
        }
        if (mPauseDummy != null) {
            mPauseDummy.setVisibility(View.INVISIBLE);
            if (mPlayer.isPlaying()) {
                mPauseDummy.setImageResource(R.drawable.pause);
            } else {
                mPauseDummy.setImageResource(R.drawable.play);
            }
        }
    }

    private void playAudio(long id) {
        sampleMediaUri = Uri.withAppendedPath(baseUri, "" + id);

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                    mPlayer.setLooping(false);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showControllerPanel();
                        }
                    });
                }
            });
            mPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    showControllerPanel();
                }
            });
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    showControllerPanel();
                }
            });

            mPlayer.setDataSource(this, sampleMediaUri);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
        // set customize skin
            case R.id.btn_customize:
                customizeFlag = true;
                mBaseLayout.setBackgroundColor(getResources()
                        .getColor(R.color.customize_back_color));

                mRew.setImageResource(R.drawable.rew);
                mFfwd.setImageResource(R.drawable.ffwd);
                mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress));

                mStartTime.setTextColor(getResources().getColor(R.color.customize_text_color));
                mEndTime.setTextColor(getResources().getColor(R.color.customize_text_color));
                updatePausePlay();
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

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mPlayer.start();
    }
}
