package com.axioma.redis;

public class MessageLogger {
   public static void logMessageReceived(final String subscriberName, final String channel, final String message) {
      System.out.println(subscriberName + " received message in channel: " + channel + ". Message: " + message);

   }

}
