
package com.example.fxos.androidtips37;

import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    public static RequestToken mRequestToken = null;

    public static OAuthAuthorization mOAuthAuthorization = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        // start OAuth authorization
        if (view.getId() == R.id.btn_start_oauth) {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    // read twitter4J settings
                    Configuration conf = ConfigurationContext.getInstance();

                    // create OAuth Object
                    mOAuthAuthorization = new OAuthAuthorization(conf);

                    // set consumer key and secret key
                    mOAuthAuthorization.setOAuthConsumer(Util.CONSUMER_KEY, Util.SECRET_KEY);
                    try {
                        // make request token object
                        mRequestToken = mOAuthAuthorization.getOAuthRequestToken(Util
                                .getCallbackUrl(MainActivity.this));
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                protected void onPostExecute(Void result) {
                    if (mRequestToken != null) {
                        // move to permission page
                        String url;
                        url = mRequestToken.getAuthorizationURL();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                }

            };
            task.execute();
        }
    }
}
