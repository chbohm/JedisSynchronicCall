package com.axioma.redis;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MyPubSub extends JedisPubSub {

   Map<MessageMatcher, MessageCallback> map = Maps.newHashMap();

   @Override
   public void onMessage(final String channel, final String message) {
      List<MessageCallback> callbacks = this.getMatchingResultCallback(channel, message);
      for (MessageCallback callback : callbacks) {
         callback.onMessage(new JSONObject(message));
      }
   }

   public void registerCallBack(final MessageMatcher matcher, final MessageCallback callback) {
      this.map.put(matcher, callback);
   }

   private List<MessageCallback> getMatchingResultCallback(final String channel, final String message) {
      List<MessageCallback> matchingCallbacks = Lists.newArrayList();
      for (MessageMatcher matcher : this.map.keySet()) {
         if (matcher.matches(channel, message)) {
            matchingCallbacks.add(this.map.get(matcher));
         }
      }
      return matchingCallbacks;
   }

}
