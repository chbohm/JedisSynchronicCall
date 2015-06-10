package com.axioma.redis;

public interface MessageMatcher {

   public boolean matches(String channel, String message);

}
