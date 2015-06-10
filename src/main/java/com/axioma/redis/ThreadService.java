package com.axioma.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadService {

   private static ExecutorService service = Executors.newCachedThreadPool();

   public static ExecutorService getService() {
      return service;
   }

}
