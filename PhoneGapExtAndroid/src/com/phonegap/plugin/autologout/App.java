package com.phonegap.plugin.autologout;

import com.phonegap.*;

import android.os.Bundle;

public class App extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        appView.getSettings().setJavaScriptEnabled(true);
    }
}