package com.axioma.eros;

import org.json.JSONException;
import org.json.JSONObject;

import com.axioma.eros.worker.ConcatWorker;
import com.axioma.redis.JSONMessageMatcher;
import com.axioma.redis.MessageCallback;
import com.axioma.redis.RedisHandler;
import com.axioma.redis.ThreadService;

public class ErosMessageEntryPoint {

   public static final String CHANNEL_ENTRY_POINT = "RequestChannel";

   public ErosMessageEntryPoint() {
      super();
      TaskRequestMessageCallback subs = new TaskRequestMessageCallback();
      JSONMessageMatcher matcher = new JSONMessageMatcher() {
         @Override
         protected boolean matches(final String channel, final JSONObject obj) {
            return CHANNEL_ENTRY_POINT.equals(channel) && obj.has("command");
         }
      };

      RedisHandler.getInstance().registerCallback(matcher, new TaskRequestMessageCallback());
      System.out.println("Eros listening requests in channel: " + CHANNEL_ENTRY_POINT);
   }

   public static void main(final String[] args) {
      new ErosMessageEntryPoint();
   }

   private static class TaskRequestMessageCallback implements MessageCallback {

      @Override
      public void onMessage(final JSONObject json) {

         try {
            String command = json.getString("command");
            String resultChannel = json.getString("resultChannel");
            Runnable processor = () -> {};

            if (command.equals("concat")) {
               processor = new ConcatWorker(resultChannel, json);
            }
            ThreadService.getService().submit(processor);
         } catch (JSONException e) {
            System.out.println("Message skipped: " + e.getMessage());
         }

      }
   }

}
