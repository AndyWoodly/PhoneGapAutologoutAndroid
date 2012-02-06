package com.canoo.phonegap.ext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

public class ExtTestPlugin extends Plugin {

	private static final String ACTION = "test";
	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {
		PluginResult result = null;
		if (ACTION.equals(action)) {
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("param", "Got param: "+data.getString(0));
				result = new PluginResult(Status.OK, jsonObj);
			} catch (JSONException e) {
				Log.e("TestPlugin", "JSON plugin error", e);
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		} else {
			result = new PluginResult(Status.INVALID_ACTION);
			Log.d("DirectoryListPlugin", "Invalid action : "+action+" passed");
		}
		return result;
	}

}
