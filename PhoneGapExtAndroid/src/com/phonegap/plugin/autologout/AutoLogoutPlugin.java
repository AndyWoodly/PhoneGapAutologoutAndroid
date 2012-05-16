package com.phonegap.plugin.autologout;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

public class AutoLogoutPlugin extends Plugin {

	private static final String LOG_TAG = "AutoLogoutPlugin";
	private static final boolean DEBUG = false;
	
	private static final String START = "startAutoLogout";

    private ScheduledThreadPoolExecutor executor;
	private String callback;
	private LogoutChecker checker;

    @Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result;
		if (START.equals(action)) {
			JSONObject jsonObj = new JSONObject();

			try {
				JSONObject params = data.getJSONObject(0);
                callback = prepareCallback(params.getString("callback"));
                final int timeoutInSeconds = params.getInt("timeOutInSeconds");
                initTimer(timeoutInSeconds);

				result = new PluginResult(Status.OK, jsonObj);
			} catch (JSONException e) {
				Log.e(LOG_TAG, "JSON plugin error", e);
				result = new PluginResult(Status.JSON_EXCEPTION);
			}

		} else {
			result = new PluginResult(Status.INVALID_ACTION);
			Log.d(LOG_TAG, "Invalid action : "+action+" passed");
		}
		return result;
	}
    
    private String prepareCallback(String callback) {
    	if (!callback.endsWith("()")) {
    		return callback+"()";
    	}
    	return callback;
    }

    @Override
    public void setView(WebView webView) {
        super.setView(webView);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                resetTimer();
                // return FALSE is important to NOT consume the event!
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
    	executor.shutdownNow();
    	super.onDestroy();
    }
    
    private void initTimer(int timeoutInSeconds) {
        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(1);
        }
		
        checker = new LogoutChecker(executor, timeoutInSeconds, TimeUnit.SECONDS);
        checker.reset();
        checker.reschedule(TimeUnit.SECONDS.toMillis(timeoutInSeconds));
		
        if (DEBUG) Log.d(LOG_TAG, "checker initialized, callback: "+callback);
    }
    
    private void resetTimer() {
		if (DEBUG) Log.d(LOG_TAG, "reset called");
    	checker.reset();
    }
    
    private void logout() {
		if (DEBUG) Log.d(LOG_TAG, "logout called");
    	sendJavascript(callback);
    }

    private class LogoutChecker implements Runnable {
		
    	private final ScheduledThreadPoolExecutor executor;
    	private final long timeOutInMillis;
    	
    	private long nextLogoutInMillis;
    	
		public LogoutChecker(ScheduledThreadPoolExecutor executor, long timeOut, TimeUnit timeUnit) {
			this.executor = executor;
			this.timeOutInMillis = timeUnit.toMillis(timeOut);
		}

		public synchronized void reset() {
			final long now = System.currentTimeMillis();
			this.nextLogoutInMillis = now + timeOutInMillis;
		}
		
		public synchronized void reschedule(long whenInMillis) {
			executor.remove(LogoutChecker.this);
			executor.schedule(LogoutChecker.this, whenInMillis, TimeUnit.MILLISECONDS);
			if (DEBUG) Log.d(LOG_TAG, "timer (re)scheduled at "+whenInMillis);
		}

		public synchronized void run() {
			if (DEBUG) Log.d(LOG_TAG, "checking logout ...");
			long now = System.currentTimeMillis();
			if (now >= nextLogoutInMillis) {
				logout();
			} else {
				long nextCheckInMillis = nextLogoutInMillis - now;
				reschedule(nextCheckInMillis);
			}
		}
		
	};    
    
}