package com.axioma.mac.erosconnector;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.mutable.MutableObject;
import org.json.JSONObject;

import com.axioma.eros.ErosMessageEntryPoint;
import com.axioma.redis.RedisCallbackCall;
import com.axioma.redis.ResultCallback;

public class ErosConnectorRedisSynchronicImpl implements ErosConnector {

   @Override
   public String concat(final List<String> list) {
      JSONObject message = new JSONObject();
      message.put("command", "concat");
      for (String str : list) {
         message.append("data", str);
      }

      CountDownLatch l = new CountDownLatch(1);
      MutableObject<String> str = new MutableObject<String>();
      ResultCallback c = new ResultCallback() {

         @Override
         public void onResult(final JSONObject resultJson) {
            str.setValue(resultJson.getString("result"));
            l.countDown();
         }
      };
      RedisCallbackCall.call(ErosMessageEntryPoint.CHANNEL_ENTRY_POINT, message, c);
      try {
         l.await();
      } catch (InterruptedException e) {
         str.setValue(e.getMessage());
         e.printStackTrace();
      }

      return str.toString();
   }
}
