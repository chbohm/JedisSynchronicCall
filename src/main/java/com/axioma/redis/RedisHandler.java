package com.axioma.redis;

import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class RedisHandler {

   private final Jedis publisher;
   private final String redisServerURL = "hrsuat.hexacta.com";

   private static RedisHandler PUBLISHER;

   public RedisHandler() {
      this.publisher = new Jedis(this.redisServerURL);
   }

   public static RedisHandler getInstance() {
      if (PUBLISHER == null) {
         PUBLISHER = new RedisHandler();
      }
      return PUBLISHER;
   }

   public void publish(final String channel, final String message) {
      try {
         //         System.out.println("Publishing into channel: " + channel + ". Message: " + message);
         this.publisher.publish(channel, message);
      } catch (JedisConnectionException e) {
         System.out.println(e.getMessage());
         System.out.println("Reconnecting to REDIS server " + this.redisServerURL + " and trying again.");
         this.reconnect();
         this.publish(channel, message);
      } catch (JedisException e) {
         System.out.println("Error publishing: " + message);
         System.out.println(e.getMessage());
         System.out.println("Message lost");
      }
   }

   public synchronized void subscribe(final JedisPubSub subs, final String channel) {
      String threadName = "ChannelListener: " + channel;
      CountDownLatch latch = new CountDownLatch(1);
      Thread t = new Thread(() -> {
         latch.countDown();
         Jedis subscriber = new Jedis(this.redisServerURL);
         subscriber.subscribe(subs, channel);
      }, threadName);
      t.start();
      try {
         latch.await();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   public synchronized void unsubscribe(final JedisPubSub subs) {
      subs.unsubscribe();
   }

   private void reconnect() {
      this.publisher.disconnect();
      try {
         System.out.print("Connecting... ");
         this.publisher.connect();
         System.out.println("OK!");
      } catch (JedisConnectionException e) {
         System.out.println("Failed. Retrying in 5 seconds...");
         try {
            Thread.sleep(5000);
         } catch (InterruptedException e1) {
            e1.printStackTrace();
         }
         this.reconnect();
      }
   }

}
