package com.axioma.mac;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

import com.axioma.eros.ErosMessageEntryPoint;
import com.axioma.redis.RedisCallbackCall;
import com.axioma.redis.RedisSynchronicCall;
import com.axioma.redis.ResultCallback;

public class MacAPI {
   public static String integerToString(final List<Integer> numbers) {
      JSONObject message = new JSONObject();
      message.put("command", "integerToString");
      for (int n : numbers) {
         message.append("data", n);
      }
      JSONObject result = RedisSynchronicCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message);
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

   public static void concat2(final List<String> strs, final ResultCallback c) {
      JSONObject message = new JSONObject();
      message.put("command", "concat");
      for (String n : strs) {
         message.append("data", n);
      }

      MutableObject<String> str = new MutableObject<>();
      RedisCallbackCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message, c);
   }
}
