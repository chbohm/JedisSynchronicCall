package com.axioma.mac;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.JedisPubSub;

import com.axioma.mac.worker.IntegerToStringWorker;
import com.axioma.redis.MessageLogger;
import com.axioma.redis.RedisHandler;

public class MacMessageEntryPoint {

   public static final String CHANNEL_ENTRY_POINT = "RequestChannel";

   public MacMessageEntryPoint() {
      super();
      TaskRequestMessageSubscriber subs = new TaskRequestMessageSubscriber();
      RedisHandler.getInstance().subscribe(subs, CHANNEL_ENTRY_POINT);
      System.out.println("MAC listening requests in channel: " + CHANNEL_ENTRY_POINT);
   }

   public static void main(final String[] args) {
      new MacMessageEntryPoint();
   }

   private static class TaskRequestMessageSubscriber extends JedisPubSub {

      @Override
      public void onMessage(final String channel, final String message) {
         JSONObject json = new JSONObject(message);
         if (!json.has("command")) {
            return;
         }
         MessageLogger.logMessageReceived("MAC Entry Point", channel, message);

         try {
            String command = json.getString("command");
            String resultChannel = json.getString("resultChannel");
            Runnable processor = () -> System.out.println("Message skipped. No processor found for this message");

            if (command.equals("integerToString")) {
               processor = new IntegerToStringWorker(resultChannel, json);
            }
            Thread processingThread = new Thread(processor);
            processingThread.start();
         } catch (JSONException e) {
            System.out.println("Message skipped: " + e.getMessage());
         }
      }
   }
}