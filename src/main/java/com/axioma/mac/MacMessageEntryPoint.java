package com.axioma.mac;

import org.json.JSONException;
import org.json.JSONObject;

import com.axioma.log.Logger;
import com.axioma.mac.worker.IntegerToStringWorker;
import com.axioma.redis.Constants;
import com.axioma.redis.JSONMessageMatcher;
import com.axioma.redis.MessageCallback;
import com.axioma.redis.RedisHandler;
import com.axioma.redis.ThreadService;

public class MacMessageEntryPoint {

   public static final String CHANNEL_ENTRY_POINT = Constants.Channels.TASK_REQUEST;

   public MacMessageEntryPoint() {
      super();
      TaskRequestMessageCallback subs = new TaskRequestMessageCallback();
      JSONMessageMatcher matcher = new JSONMessageMatcher() {
         @Override
         protected boolean matches(final String channel, final JSONObject obj) {
            return CHANNEL_ENTRY_POINT.equals(channel) && obj.has("command");
         }
      };

      RedisHandler.getInstance().registerCallback(matcher, new TaskRequestMessageCallback());
      System.out.println("MAC listening requests in channel: " + CHANNEL_ENTRY_POINT);
   }

   public static void main(final String[] args) {
      Logger log = Logger.getInstance();
      log.init(args[0]);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         log.close();
      }));

      new MacMessageEntryPoint();
   }

   private static class TaskRequestMessageCallback implements MessageCallback {

      @Override
      public void onMessage(final JSONObject resultJson) {

         try {
            String command = resultJson.getString("command");
            String resultChannel = resultJson.getString("resultChannel");
            Runnable processor = () -> {
            };

            if (command.equals("integerToString")) {
               processor = new IntegerToStringWorker(resultChannel, resultJson);
            }

            ThreadService.getService().submit(processor);
         } catch (JSONException e) {
            System.out.println("Message skipped: " + e.getMessage());
         }

      }
   }
}