package com.axioma.mac.erosconnector;

import java.util.List;

import org.json.JSONObject;

import com.axioma.eros.ErosMessageEntryPoint;
import com.axioma.redis.RedisSynchronicCall;

public class ErosConnectorRedisSynchronicImpl implements ErosConnector {

   @Override
   public String concat(final List<String> list) {
      JSONObject message = new JSONObject();
      message.put("command", "concat");
      for (String str : list) {
         message.append("data", str);
      }
      JSONObject result = RedisSynchronicCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message);
      return result == null ? "" : result.getString("result");
   }
}
