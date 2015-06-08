package com.axioma.mac.worker;

import redis.clients.jedis.JedisPubSub;

import com.axioma.redis.RedisHandler;
import com.axioma.redis.ResultProvider;

public class TimeStampProvider implements ResultProvider {

   @Override
   public Object getResult() {
      RedisHandler.getInstance().subscribe(new JedisPubSub() {
         @Override
         public void onMessage(final String channel, final String message) {
            super.onMessage(channel, message);
         }
      }, "");
      return "";
   }
}
