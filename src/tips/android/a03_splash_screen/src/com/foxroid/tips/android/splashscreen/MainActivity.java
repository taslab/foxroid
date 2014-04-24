
package com.foxroid.tips.android.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.foxroid.tips.android.splashscreen.R;

public class MainActivity extends Activity implements AnimationListener {
    private TextView txtSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation transition = AnimationUtils.loadAnimation(this, R.anim.transition_in);
        transition.setAnimationListener(this);

        txtSplash = (TextView)findViewById(R.id.txt_splash);
        // start splash
        txtSplash.startAnimation(transition);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // move next activity
        Intent intent = new Intent(this, AfterSplashActivity.class);
        startActivity(intent);
        // finish this activity
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // define
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // start animation
        // Define and services you want to run in the background
    }
}
