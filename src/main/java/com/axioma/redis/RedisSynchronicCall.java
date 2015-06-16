package com.axioma.redis;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

public class RedisSynchronicCall {
   private static Random random = new Random(System.currentTimeMillis());

   public synchronized static JSONObject call(final String serviceChannel, final JSONObject requestMessage) {

      Future<JSONObject> future = ThreadService.getService().submit(new Callable<JSONObject>() {

         @Override
         public synchronized JSONObject call() throws Exception {
            String requestId = System.nanoTime() + " " + random.nextInt();
            requestMessage.put("resultChannel", serviceChannel);
            requestMessage.put("requestId", requestId);

            MutableObject<JSONObject> result = new MutableObject<>();
            Callable<JSONObject> self = this;

            JSONMessageMatcher matcher = new JSONMessageMatcher() {
               @Override
               protected boolean matches(final String channel, final JSONObject obj) {
                  boolean matches =
                           channel.equals(serviceChannel) && obj.getString("requestId").equals(requestId) && obj.has("result");
                  return matches;
               }
            };
            MessageCallback callback = new MessageCallback() {

               @Override
               public void onMessage(final JSONObject resultJson) {
                  result.setValue(resultJson);
                  synchronized (self) {
                     self.notifyAll();
                  }

               }
            };
            RedisHandler.getInstance().registerCallback(matcher, callback);
            RedisHandler.getInstance().publish(serviceChannel, requestMessage.toString());
            this.wait(3000);
            //            RedisHandler.getInstance().unsubscribe(pubSub);
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
         e.printStackTrace();
      }
      return null;
   }
}
