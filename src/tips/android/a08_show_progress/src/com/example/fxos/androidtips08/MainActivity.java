
package com.example.fxos.androidtips08;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int MAX_PROGRESS = 100;

    private static final int NEXT_PROGRESS_DELAY = 50;

    private ProgressBar mProgressBar;

    private LinearLayout mProgressLayout;

    private ProgressRunnable mProgressRunnable;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set feature to show title bar progress
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // hide title bar progress first
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setProgressBarIndeterminateVisibility(false);
            }
        });

        setContentView(R.layout.activity_main);

        mProgressLayout = (LinearLayout)findViewById(R.id.progress_layout);
        mProgressLayout.setVisibility(View.GONE);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mProgressBar.setMax(MAX_PROGRESS);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_show_progress) {
            // show progress items
            mProgressLayout.setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(true);

            // check if progress thread is not running
            if (mProgressRunnable == null) {
                // set progress thread
                mProgressRunnable = new ProgressRunnable();
                mProgressBar.setProgress(0);

                // start progress thread
                mHandler.postDelayed(mProgressRunnable, NEXT_PROGRESS_DELAY);
            }
        }
    }

    class ProgressRunnable implements Runnable {
        @Override
        public void run() {
            int progress = mProgressBar.getProgress();
            // increment progress
            if (mProgressBar.getProgress() < MAX_PROGRESS) {
                progress++;
                mProgressBar.setProgress(progress);
                mHandler.postDelayed(this, NEXT_PROGRESS_DELAY);
            } else {
                // hide progress items
                mProgressLayout.setVisibility(View.GONE);
                setProgressBarIndeterminateVisibility(false);

                // show finish message
                Toast.makeText(MainActivity.this, R.string.toast_message, Toast.LENGTH_SHORT)
                        .show();

                mProgressRunnable = null;
            }
        }
    }
}
