package com.axioma.mac;

import java.util.List;

import org.json.JSONObject;

import com.axioma.eros.ErosMessageEntryPoint;
import com.axioma.redis.RedisSynchronicCall;

public class MacAPI {
   public static String integerToString(final List<Integer> numbers) {
      JSONObject message = new JSONObject();
      message.put("command", "integerToString");
      for (int n : numbers) {
         message.append("data", n);
      }
      JSONObject result = RedisSynchronicCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message);
      System.out.println(result);
      return result == null ? "" : result.getString("result");
   }

   public static String concat(final List<String> strs) {
      JSONObject message = new JSONObject();
      message.put("command", "concat");
      for (String n : strs) {
         message.append("data", n);
      }
      JSONObject result = RedisSynchronicCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message);
      return result == null ? "" : result.getString("result");
   }
}
