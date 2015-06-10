package com.axioma.loggingapp;

import org.json.JSONObject;

import com.axioma.mac.worker.TaskWorker;
import com.axioma.redis.Constants;
import com.axioma.redis.JSONMessageMatcher;
import com.axioma.redis.MessageCallback;
import com.axioma.redis.RedisHandler;
import com.axioma.redis.ThreadService;

public class LoggingAppServerEntryPoint {

   public LoggingAppServerEntryPoint() {
      super();
      JSONMessageMatcher matcher = new JSONMessageMatcher() {
         @Override
         protected boolean matches(final String channel, final JSONObject json) {
            if (json.has("type") && json.has("taskName")) {
               String type = json.getString("type");
               if (Constants.TaskRequestJSon.TYPE_REQUEST.equals(type)) {
                  return true;
               }
            }
            return false;
         }

      };
      MessageCallback callback = new MessageCallback() {

         @Override
         public void onMessage(final JSONObject json) {
            String processName = json.getString("taskName");
            Runnable processor = () -> {
            };
            if (processName.startsWith("doRA")) {
               processor = new TaskWorker("RA", json);

            } else if (processName.startsWith("doPA")) {
               processor = new TaskWorker("PA", json);
            }
            ThreadService.getService().submit(processor);
         }

      };
      RedisHandler.getInstance().registerCallback(matcher, callback);
      System.out.println("LoggingApp Server listening requests");
   }

   public static void main(final String[] args) {
      new LoggingAppServerEntryPoint();
   }

}
