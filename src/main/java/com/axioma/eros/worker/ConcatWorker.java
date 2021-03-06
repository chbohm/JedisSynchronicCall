package com.axioma.eros.worker;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.axioma.eros.api.Utils;
import com.axioma.log.Logger;
import com.axioma.redis.RedisHandler;
import com.google.common.collect.Lists;

public class ConcatWorker implements Runnable {

   private final String resultChannel;
   private final List<String> strToConcat;
   private final String requestId;

   public ConcatWorker(final String resultChannel, final JSONObject json) {
      super();
      this.resultChannel = resultChannel;
      this.strToConcat = this.getStrToConcat(json);
      this.requestId = json.getString("requestId");
   }

   private List<String> getStrToConcat(final JSONObject json) {
      List<String> data = Lists.newArrayList();
      JSONArray array = json.getJSONArray("data");
      for (int i = 0; i < array.length(); i++) {
         data.add(array.getString(i));
      }
      return data;
   }

   @Override
   public void run() {
      Logger.getInstance().log("ConcatWorker", "start", this.requestId, "PROCESSING");
      String result = Utils.concat(this.strToConcat);
      this.sendResult(result);
      Logger.getInstance().log("ConcatWorker", "end", this.requestId, "PROCESSING");
   }

   private void sendResult(final String result) {
      JSONObject message = new JSONObject();
      message.put("result", result);
      message.put("messageId", this.requestId);
      RedisHandler.getInstance().publish(this.resultChannel, message.toString());
   }

}
