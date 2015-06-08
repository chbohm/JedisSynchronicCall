package com.axioma.redis;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.axioma.mac.worker.TaskWorker;

public class SevenXSubscriber {

   public SevenXSubscriber() {
      super();
      Jedis messageSubscriberClient = new Jedis("redis.hexacta.com");
      TaskRequestMessageSubscriber subs = new TaskRequestMessageSubscriber();
      Thread t = new Thread(() -> {
         messageSubscriberClient.subscribe(subs, Constants.Channels.TASK_REQUEST);
      });

      t.start();
      System.out.println("Ready");
      //      subs.onMessage("", "{type:request, taskName: doRA, processId:289}");
   }

   public static void main(final String[] args) {
      new SevenXSubscriber();
   }

   private static class TaskRequestMessageSubscriber extends JedisPubSub {

      @Override
      public void onMessage(final String channel, final String message) {
         JSONObject json = new JSONObject(message);
         String type = json.getString("type");
         if (!Constants.TaskRequestJSon.TYPE_REQUEST.equals(type)) {
            return;
         }
         String processName = json.getString("taskName");
         TaskWorker processor = null;
         if (processName.startsWith("doRA")) {
            processor = new TaskWorker("RA", json);

         } else if (processName.startsWith("doPA")) {
            processor = new TaskWorker("PA", json);
         }
         if (processor != null) {
            Thread processingThread = new Thread(processor);
            processingThread.start();
         }
      }
   }
}
