package com.axioma.redis;

import org.json.JSONObject;

public interface MessageCallback {

   public void onMessage(JSONObject resultJson);

}
