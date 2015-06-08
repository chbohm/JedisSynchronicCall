package com.axioma.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

public class Job {

   private final String statusQueue;
   private final String resultsQueue;

   public Job(final String statusQueue, final String resultsQueue) {
      this.statusQueue = statusQueue;
      this.resultsQueue = resultsQueue;
   }

   public void process(final String requestName) {
      try {
         Random r = new Random();
         RabbitMQHandler handler = RabbitMQHandler.getInstance();

         List<Long> statusCheckpoints = Lists.newArrayList(0l, 10l, 20l, 30l, 40l, 50l, 60l, 70l, 80l, 90l);
         Long lastCheckpoint = -1l;
         double maxStep = 5000;

         for (int i = 0; i < maxStep; i++) {
            Long currentProgressCheckPoint = Math.round((i / maxStep) * 100);
            if (statusCheckpoints.contains(currentProgressCheckPoint) && lastCheckpoint != currentProgressCheckPoint) {
               handler.getChannel().basicPublish("", this.statusQueue, null, currentProgressCheckPoint.toString().getBytes());
               lastCheckpoint = currentProgressCheckPoint;
            }
            String data = String.format("{id:%d, data: %f}", i, r.nextDouble());
            handler.getChannel().basicPublish("", this.resultsQueue, null, data.getBytes());
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

}
