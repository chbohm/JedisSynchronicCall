package com.axioma.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

public class SynchronizedRedisCall {
   private static int requestId = 0;

   public synchronized static JSONObject call(final String serviceChannel, final JSONObject message) {
      requestId++;
      ExecutorService service = Executors.newSingleThreadExecutor();
      String resultChannel = "channel" + requestId;
      message.put("resultChannel", resultChannel);

      Future<JSONObject> future = service.submit(new Callable<JSONObject>() {

         @Override
         public synchronized JSONObject call() throws Exception {
            MutableObject<JSONObject> result = new MutableObject<>();
            Callable<JSONObject> self = this;
            RedisHandler.getInstance().subscribe(new JedisPubSub() {
               @Override
               public void onMessage(final String channel, final String message) {
                  MessageLogger.logMessageReceived("Channel Subscriber", channel, message);
                  result.setValue(new JSONObject(message));
                  RedisHandler.getInstance().unsubscribe(this);
                  synchronized (self) {
                     self.notifyAll();
                  }
               }
            }, resultChannel);
            RedisHandler.getInstance().publish(serviceChannel, message.toString());
            this.wait();
            JSONObject answer = result.getValue();
            return answer;
         }
      });

      try {
         //         JSONObject result = future.get(10, TimeUnit.SECONDS);
         JSONObject result = future.get();
         return result;
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      }
      //         catch (TimeoutException e) {
      //         System.out.println("There was no result for: " + message);
      //      }
      return null;
   }

}
