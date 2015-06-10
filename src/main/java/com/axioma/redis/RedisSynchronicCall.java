package com.axioma.redis;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

public class RedisSynchronicCall {
   private static Random random = new Random(System.currentTimeMillis());
   private static ExecutorService service = Executors.newSingleThreadExecutor();

   public synchronized static JSONObject call(final String serviceChannel, final JSONObject requestMessage) {

      Future<JSONObject> future = service.submit(new Callable<JSONObject>() {

         @Override
         public synchronized JSONObject call() throws Exception {
            String requestId = System.nanoTime() + " " + random.nextInt();
            String resultChannel = "channel" + requestId;
            requestMessage.put("resultChannel", resultChannel);
            requestMessage.put("messageId", requestId);

            MutableObject<JSONObject> result = new MutableObject<>();
            Callable<JSONObject> self = this;
            final JedisPubSub pubSub = new JedisPubSub() {
               @Override
               public void onMessage(final String channel, final String resultMessage) {

                  JSONObject resultJson = new JSONObject(resultMessage);
                  if (resultJson.getString("messageId").equals(requestId)) {
                     result.setValue(resultJson);
                  } else {
                     System.out.println("Received result from a different process. Expected: " + requestId + " but was: "
                              + resultJson.getString("messageId") + " channel: " + resultChannel);
                     System.out.println("Request: " + requestMessage);
                     System.out.println("Answer: " + resultMessage);
                  }
                  synchronized (self) {
                     self.notifyAll();
                  }

               }
            };
            RedisHandler.getInstance().subscribe(pubSub, resultChannel);
            RedisHandler.getInstance().publish(serviceChannel, requestMessage.toString());
            this.wait(3000);
            RedisHandler.getInstance().unsubscribe(pubSub);
            JSONObject answer = result.getValue();
            return answer;
         }
      });

      try {
         JSONObject result = future.get(3, TimeUnit.SECONDS);
         //         JSONObject result = future.get();
         return result;
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      } catch (TimeoutException e) {
         System.out.println("There was no result for: " + requestMessage);
      }
      return null;
   }

}
