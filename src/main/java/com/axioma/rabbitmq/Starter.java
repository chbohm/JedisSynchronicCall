package com.axioma.rabbitmq;

public class Starter {

   public static void main(final String[] args) {
      RabbitMQHandler.getInstance();
      addShutDownHook();
      new TaskRequirementConsumer();
   }

   private static void addShutDownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

         @Override
         public void run() {
            RabbitMQHandler.getInstance().dispose();

         }
      }));
   }

}
