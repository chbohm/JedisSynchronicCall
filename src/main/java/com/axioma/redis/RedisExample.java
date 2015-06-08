package com.axioma.redis;

import redis.clients.jedis.Jedis;

public class RedisExample {
   public static void main(final String[] args) {
      Jedis jedis = new Jedis("hrsuat.hexacta.com");

      jedis.publish("7.xChannel", "hola");
      jedis.publish("7.xChannel", "pepe");
      jedis.publish("7.xChannel", "special");


   }


}
