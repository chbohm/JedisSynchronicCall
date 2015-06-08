package com.axioma;

import org.json.JSONObject;

import com.axioma.redis.RedisHandler;
import com.axioma.redis.SynchronizedRedisCall;

public class Executor {
   public static void main(final String[] args) {

      Thread t =
               new Thread(() -> RedisHandler.getInstance().publish("RequestChannel",
                        "{command:integerToString, data:[1,2,3,4], resultChannel: executorChannel}"));
      t.start();

      //      System.out.println("Result: " + concat(1, 2, 3, 4, 5, 6));

   }

   private static String concat(final Integer... numbers) {
      JSONObject message = new JSONObject();
      message.append("command", "integerToString");
      for (Integer n : numbers) {
         message.append("data", n);
      }
      JSONObject result = SynchronizedRedisCall.call("RequestChannel", message);
      return result.getString("result");
   }
}
