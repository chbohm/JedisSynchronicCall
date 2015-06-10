package com.axioma.redis;

import org.json.JSONObject;

public class JSONMessageMatcher implements MessageMatcher {

   @Override
   public boolean matches(final String channel, final String message) {
      JSONObject obj = new JSONObject(message);
      return this.matches(channel, obj);
   }

   protected boolean matches(final String channel, final JSONObject obj) {
      return true;
   }

}
