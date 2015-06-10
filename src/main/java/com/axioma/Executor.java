package com.axioma;

import java.util.List;

import org.json.JSONObject;

import com.axioma.mac.MacAPI;
import com.axioma.redis.ResultCallback;
import com.google.common.collect.Lists;

public class Executor {
   public static void main(final String[] args) {

      //      Thread t =
      //               new Thread(() -> );
      //      t.start();

      //      System.out.println("Result: " + concat(1, 2, 3, 4, 5, 6));

      //      RedisHandler.getInstance().publish("RequestChannel",
      //               "{command:integerToString, data:[1,2,3,4], resultChannel: executorChannel}");
      concat2();

   }

   private static void intToStr() {
      List<Integer> numbers = Lists.newArrayList();
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
         long s0 = System.currentTimeMillis();
         numbers.add(i % 10);
         String result = MacAPI.integerToString(numbers);
         long s1 = System.currentTimeMillis();
         System.out.println(i + " Result: " + result + ". Elapsed time: " + (s1 - s0) + " ms");
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Response time: " + (t1 - t0) + " ms");
   }

   private static void concat() {
      List<String> numbers = Lists.newArrayList();
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < 20; i++) {
         long s0 = System.currentTimeMillis();
         numbers.add("ab");
         String result = MacAPI.concat(numbers);
         long s1 = System.currentTimeMillis();
         System.out.println(i + " Result: " + result + ". Elapsed time: " + (s1 - s0) + " ms");
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Response time: " + (t1 - t0) + " ms");
   }

   private static void concat2() {
      List<String> numbers = Lists.newArrayList();
      long t0 = System.currentTimeMillis();
      MyResultCallback c = null;
      for (int i = 0; i < 20; i++) {
         c = new MyResultCallback(i);
         numbers.add("ab");
         MacAPI.concat2(numbers, c);
      }
   }

   private static class MyResultCallback implements ResultCallback {
      private final long s0 = System.currentTimeMillis();
      private final int i;

      public MyResultCallback(final int i) {
         super();
         this.i = i;
      }

      @Override
      public void onResult(final JSONObject resultJson) {
         String result = resultJson.getString("result");
         long s1 = System.currentTimeMillis();
         System.out.println(this.i + " Result: " + result + ". Elapsed time: " + (s1 - this.s0) + " ms");
      }

   }
}
