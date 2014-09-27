/**
 * 
 */
package com.tmser.train;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author tjx
 * @version 2.0
 * 2014-9-27
 */
public abstract class JSONUtil {
	public static String getString(JSONObject json, String key){
		try {
			return json.getString(key) == null ? "" : json.getString(key);
		} catch (JSONException e) {
			return "";
		}
	}


public static Integer getInt(JSONObject json, String key){
	try {
		return json.getInt(key);
	} catch (JSONException e) {
		return 0;
	}
}

public static Long getLong(JSONObject json, String key){
	try {
		return json.getLong(key);
	} catch (JSONException e) {
		return 0l;
	}
}
public static JSONObject getJSONObject(JSONObject json, String key){
	try {
		return json.getJSONObject(key) == null ? null : json.getJSONObject(key);
	} catch (JSONException e) {
		return null;
	}
}

public static JSONArray getJSONArray(JSONObject json, String key){
	try {
		return json.getJSONArray(key) == null ? null : json.getJSONArray(key);
	} catch (JSONException e) {
		return null;
	}
}

public static String getString(JSONObject json, String key,String sdefault){
	try {
		return json.getString(key) == null ? "" : json.getString(key);
	} catch (JSONException e) {
		return sdefault;
	}
}
public static String getErrMsgString(JSONObject json, String key){
	try {
		JSONArray err = json.getJSONArray(key);
		StringBuilder errMsg = new StringBuilder();
		for(int i=0;i<err.length();i++){
			errMsg.append(err.getString(i)).append(",");
			
		}
		return errMsg.length() == 0 ? "" : errMsg.toString();
	} catch (JSONException e) {
		return "";
	}
}

public static boolean getBoolean(JSONObject json, String key){
	try {
		return json.getBoolean(key);
	} catch (JSONException e) {
		return false;
	}
}
}
