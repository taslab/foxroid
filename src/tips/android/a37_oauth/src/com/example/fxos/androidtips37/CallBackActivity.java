
package com.example.fxos.androidtips37;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CallBackActivity extends ListActivity {
    private AccessToken token = null;

    private ResponseList<Status> mTimeLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // get uri from twitter Authentication screen of Twitter
                Uri uri = getIntent().getData();

                if (uri != null
                        && uri.toString().startsWith(Util.getCallbackUrl(CallBackActivity.this))) {
                    // get oauth_verifier
                    String verifier = uri.getQueryParameter(Util.VERIFIER);
                    try {
                        // get AccessToken Object
                        token = MainActivity.mOAuthAuthorization.getOAuthAccessToken(
                                MainActivity.mRequestToken, verifier);

                        Twitter tw = new TwitterFactory().getInstance();
                        tw.setOAuthConsumer(Util.CONSUMER_KEY, Util.SECRET_KEY);
                        tw.setOAuthAccessToken(token);

                        // get self TimeLine
                        mTimeLine = tw.getUserTimeline(tw.getAccountSettings().getScreenName());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                // show TimeLine list
                TimeLineAdapter adapter = new TimeLineAdapter(CallBackActivity.this,
                        android.R.layout.simple_list_item_2, mTimeLine);
                getListView().setAdapter(adapter);
            }

        };
        task.execute();
    }

    class TimeLineAdapter extends ArrayAdapter<Status> {
        private int textViewResourceId;

        public TimeLineAdapter(Context context, int textViewResourceId,
                ResponseList<Status> statusArray) {
            super(context, textViewResourceId, statusArray);
            this.textViewResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(textViewResourceId, null);
            }
            Status status = getItem(position);

            // set tweet
            TextView text1 = (TextView)v.findViewById(android.R.id.text1);
            text1.setText(status.getText());

            // set user name
            TextView text2 = (TextView)v.findViewById(android.R.id.text2);
            text2.setText(status.getUser().getName());

            return v;
        }

    }
}
