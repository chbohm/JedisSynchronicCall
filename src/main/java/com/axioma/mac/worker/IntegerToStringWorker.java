package com.axioma.mac.worker;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.axioma.mac.erosconnector.ErosConnectorRedisSynchronicImpl;
import com.axioma.redis.RedisHandler;
import com.google.common.collect.Lists;

public class IntegerToStringWorker implements Runnable {

   private final List<Integer> numbers;
   private final String resultChannel;
   private final String messageId;

   public IntegerToStringWorker(final String resultChannel, final JSONObject json) {
      super();
      this.numbers = this.getNumbers(json);
      this.resultChannel = resultChannel;
      this.messageId = json.getString("messageId");
   }

   private List<Integer> getNumbers(final JSONObject json) {
      List<Integer> numbers = Lists.newArrayList();
      JSONArray integerNumbers = json.getJSONArray("data");
      for (int i = 0; i < integerNumbers.length(); i++) {
         numbers.add(integerNumbers.getInt(i));
      }
      return numbers;
   }

   @Override
   public void run() {
      List<String> strNumebrs = Lists.newArrayList();
      for (Integer n : this.numbers) {
         String str = "";
         switch (n) {
            case 0:
               str = "zero";
               break;
            case 1:
               str = "one";
               break;
            case 2:
               str = "two";
               break;
            case 3:
               str = "three";
               break;
            case 4:
               str = "four";
               break;
            case 5:
               str = "five";
               break;
            case 6:
               str = "six";
               break;
            case 7:
               str = "seven";
               break;
            case 8:
               str = "eight";
               break;
            case 9:
               str = "nine";
               break;
         }
         strNumebrs.add(str);
      }
      ErosConnectorRedisSynchronicImpl impl = new ErosConnectorRedisSynchronicImpl();
      String result = impl.concat(strNumebrs);
      this.sendResult(result);
   }

   private void sendResult(final String result) {
      JSONObject message = new JSONObject();
      message.put("result", result);
      message.put("messageId", this.messageId);
      RedisHandler.getInstance().publish(this.resultChannel, message.toString());
   }

}
