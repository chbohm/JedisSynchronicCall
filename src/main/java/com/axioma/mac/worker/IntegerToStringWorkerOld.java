package com.axioma.mac.worker;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import com.axioma.redis.RedisHandler;

public class IntegerToStringWorkerOld implements Runnable {

   private final JSONObject json;
   private final Integer processId;
   private final String processName;
   private final String data;
   private final String resultChannel;

   public IntegerToStringWorkerOld(final String processName, final String resultChannel, final JSONObject json) {
      super();
      this.processName = processName;
      this.json = json;
      this.processId = json.getInt("processId");
      this.data = json.getString("data");
      this.resultChannel = resultChannel;
   }

   @Override
   public void run() {
      try {
         Thread.currentThread().sleep(100);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      String checksum = new String(DigestUtils.getMd5Digest().digest());
      this.sendResult(checksum);
   }

   private void sendAccepted() {
      JSONObject message = new JSONObject();
      message.accumulate("type", "request_accepted");
      message.accumulate("processId", this.processId);
      RedisHandler.getInstance().publish(this.resultChannel, message.toString());
   }

   private void sendResult(final String data) {
      JSONObject message = new JSONObject();
      message.accumulate("type", "finished");
      message.accumulate("data", data);
      message.accumulate("processId", this.processId);
      RedisHandler.getInstance().publish(this.resultChannel, message.toString());
   }

   private void sendProgress(final int value) {
      JSONObject message = new JSONObject();
      message.accumulate("processId", this.processId);
      message.accumulate("type", "progress");
      message.accumulate("value", Integer.toString(value));
      message.accumulate("id", Integer.toString(value));
      message.accumulate("intermediateResult", Math.random() * 10);
      System.out.println(message.toString());
      RedisHandler.getInstance().publish(this.resultChannel, message.toString());
   }
}
