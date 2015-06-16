package com.axioma.redis;

import java.util.concurrent.CountDownLatch;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class RedisHandler {

   public static final String CHANNEL = Constants.Channels.TASK_REQUEST;

   private final Jedis publisher;
   private final String redisServerURL = "hxpws-zojeda.hexacta.com";
//   private final String redisServerURL = "hrsuat.hexacta.com";

   private final JedisPoolConfig poolConfig = new JedisPoolConfig();
   private final JedisPool jedisPool = new JedisPool(this.poolConfig, this.redisServerURL, 6379, 0);
   private final MyPubSub pubSub = new MyPubSub();

   private static RedisHandler PUBLISHER;

   public RedisHandler() {
      this.publisher = this.jedisPool.getResource();
      this.publisher.append("name", "christian");
      System.out.println(this.publisher.get("name"));
      this.subscribe();
   }

   public static RedisHandler getInstance() {
      if (PUBLISHER == null) {
         PUBLISHER = new RedisHandler();
      }
      return PUBLISHER;
   }

   public void publish(final String channel, final String message) {
      try {
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

   public void registerCallback(final MessageMatcher matcher, final MessageCallback callback) {
      this.pubSub.registerCallBack(matcher, callback);
   }

   private Jedis subscribe() {
      Jedis subscriber = this.jedisPool.getResource();
      String threadName = "ChannelListener: " + CHANNEL;
      CountDownLatch latch = new CountDownLatch(1);
      Thread t = new Thread(() -> {
         latch.countDown();
         subscriber.subscribe(this.pubSub, CHANNEL);
      }, threadName);
      t.start();
      try {
         latch.await();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      return subscriber;
   }

   public synchronized void unsubscribe(final JedisPubSub subs) {
      //      subs.unsubscribe();
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
