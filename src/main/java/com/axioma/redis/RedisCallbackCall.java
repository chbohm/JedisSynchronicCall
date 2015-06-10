package com.axioma.redis;

import java.util.Random;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

public class RedisCallbackCall {
   private static Random random = new Random(System.currentTimeMillis());

   public synchronized static void call(final String serviceChannel, final JSONObject requestMessage,
            final ResultCallback callback) {
      String requestId = System.nanoTime() + " " + random.nextInt();
      String resultChannel = serviceChannel;
      requestMessage.put("resultChannel", resultChannel);
      requestMessage.put("messageId", requestId);

      MutableObject<JSONObject> result = new MutableObject<>();
      final JedisPubSub pubSub = new JedisPubSub() {
         @Override
         public void onMessage(final String channel, final String resultMessage) {

            JSONObject resultJson = new JSONObject(resultMessage);
            if (!resultJson.has("messageId")) {
               return;
            }

            if (resultJson.getString("messageId").equals(requestId)) {
               result.setValue(resultJson);
               callback.onResult(resultJson);
            } else {
               System.out.println("Received result from a different process. Expected: " + requestId + " but was: "
                        + resultJson.getString("messageId") + " channel: " + resultChannel);
               System.out.println("Request: " + requestMessage);
               System.out.println("Answer: " + resultMessage);
            }
         }
      };
      RedisHandler.getInstance().publish(serviceChannel, requestMessage.toString());
   }

}
