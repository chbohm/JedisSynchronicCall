package com.axioma.mac.worker;

import org.json.JSONObject;

import com.axioma.redis.Constants;
import com.axioma.redis.RedisHandler;
import com.axioma.redis.Constants.Channels;

public class TaskWorker implements Runnable {

   private final JSONObject json;
   private final Integer processId;
   private final String processName;

   public TaskWorker(final String processName, final JSONObject json) {
      super();
      this.processName = processName;
      this.json = json;
      this.processId = json.getInt("processId");
   }

   @Override
   public void run() {
      this.sendAccepted();
      for (int i = 0; i < 100; i++) {
         this.sendProgress(i);
         try {
            Thread.currentThread().sleep(100);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      this.sendFinished();
   }

   private void sendAccepted() {
      JSONObject message = new JSONObject();
      message.accumulate("type", "request_accepted");
      message.accumulate("processId", this.processId);
      RedisHandler.getInstance().publish(Constants.Channels.TASK_REQUEST_FEEDBACK, message.toString());
   }

   private void sendFinished() {
      JSONObject message = new JSONObject();
      message.accumulate("type", "finished");
      message.accumulate("data", "values");
      message.accumulate("processId", this.processId);
      RedisHandler.getInstance().publish(Constants.Channels.TASK_REQUEST_FEEDBACK, message.toString());
   }

   private void sendProgress(final int value) {
      JSONObject message = new JSONObject();
      message.accumulate("processId", this.processId);
      message.accumulate("type", "progress");
      message.accumulate("value", Integer.toString(value));
      message.accumulate("id", Integer.toString(value));
      message.accumulate("intermediateResult", Math.random() * 10);
      System.out.println(message.toString());
      RedisHandler.getInstance().publish(Constants.Channels.TASK_REQUEST_FEEDBACK, message.toString());
   }
}
