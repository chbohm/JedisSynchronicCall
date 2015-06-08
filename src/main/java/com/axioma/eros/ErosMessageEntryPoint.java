package com.axioma.eros;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

import com.axioma.eros.worker.ConcatWorker;
import com.axioma.redis.MessageLogger;
import com.axioma.redis.RedisHandler;

public class ErosMessageEntryPoint {

   public static final String CHANNEL_ENTRY_POINT = "RequestChannel";

   public ErosMessageEntryPoint() {
      super();
      TaskRequestMessageSubscriber subs = new TaskRequestMessageSubscriber();
      RedisHandler.getInstance().subscribe(subs, CHANNEL_ENTRY_POINT);
      System.out.println("Eros listening requests in channel: " + CHANNEL_ENTRY_POINT);
   }

   public static void main(final String[] args) {
      new ErosMessageEntryPoint();
   }

   private static class TaskRequestMessageSubscriber extends JedisPubSub {

      @Override
      public void onMessage(final String channel, final String message) {
         MessageLogger.logMessageReceived("Eros Entry Point", channel, message);
         JSONObject json = new JSONObject(message);
         try {
            String command = json.getString("command");
            String resultChannel = json.getString("resultChannel");
            Runnable processor = () -> System.out.println("Message skipped. No processor found for this message");

            if (command.equals("concat")) {
               processor = new ConcatWorker(resultChannel, json);
            }
            Thread processingThread = new Thread(processor);
            processingThread.start();
         } catch (JSONException e) {
            System.out.println("Message skipped: " + e.getMessage());
         }
      }
   }
}
