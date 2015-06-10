package com.axioma;

import java.util.List;
import java.util.Random;

import com.axioma.mac.MacAPI;
import com.google.common.collect.Lists;

public class Executor {
   public static void main(final String[] args) {
      intToStr();
   }

   private static void intToStr() {
      long t0 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
         List<Integer> request = createRequest();
         long s0 = System.currentTimeMillis();
         String result = MacAPI.integerToString(request);
         long s1 = System.currentTimeMillis();
         System.out.println(i + " Elapsed time: " + (s1 - s0) + " ms. Request: " + request + ". Result: " + result);
      }
      long t1 = System.currentTimeMillis();
      System.out.println("Response time: " + (t1 - t0) + " ms");
   }

   private static List<Integer> createRequest() {
      Random r = new Random();
      return Lists.newArrayList(r.nextInt(10), r.nextInt(10), r.nextInt(10), r.nextInt(10), r.nextInt(10), r.nextInt(10),
               r.nextInt(10), r.nextInt(10), r.nextInt(10));
   }

}
