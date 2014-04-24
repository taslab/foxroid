
package com.example.fxos.androidtips20;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;

public class MainActivity extends Activity {
    private WebView mWebview;

    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebview = (WebView)findViewById(R.id.webview);

        // set loading page
        html = "<iframe width=\"100%\" height=\"315\" src=\"http://www.youtube.com/embed/DjjkAYI5kNM\" frameborder=\"0\" allowfullscreen></iframe>";

        WebSettings settings = mWebview.getSettings();
        // set JavaScript and Plug-In
        settings.setJavaScriptEnabled(true);
        mWebview.getSettings().setPluginState(PluginState.ON);

        // set blank WebChromeClient to work successfully
        mWebview.setWebChromeClient(new WebChromeClient());

    }

    protected void onResume() {
        super.onResume();
        // load data
        mWebview.loadData(html, "text/html", null);
    }

    protected void onPause() {
        super.onPause();
        // stop movie
        mWebview.loadData("", "text/html", null);
    }
}
