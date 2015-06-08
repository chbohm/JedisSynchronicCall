package com.axioma.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQHandler {

   private static RabbitMQHandler HANDLER = null;
   private ConnectionFactory factory;
   private Connection connection;
   private Channel channel;

   public static RabbitMQHandler getInstance() {
      if (HANDLER == null) {
         HANDLER = new RabbitMQHandler();
      }
      return HANDLER;
   }

   public RabbitMQHandler() {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = null;
      Channel channel = null;
      try {
         connection = factory.newConnection();
         channel = connection.createChannel();
         channel.queueDeclare(QueueNames.TASK_REQUIREMENT_QUEUE_NAME, false, false, false, null);
         channel.basicQos(1);
      } catch (Exception e) {
         this.dispose();
         throw new RuntimeException(e);
      }
   }

   public void queueDeclare(final String name) throws IOException {
      this.channel.queueDeclare(name, false, false, false, null);
   }

   public void publish(final String queueName, final String message) {
      try {
         this.channel.basicPublish("", QueueNames.TASK_REQUIREMENT_QUEUE_NAME, null, message.getBytes());
         System.out.println(" [x] Sent '" + message + "'");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public Channel getChannel() {
      return this.channel;
   }

   public void dispose() {
      System.out.println("Disposing RabbitMQ connection and channel");
      try {
         if (this.channel != null) {
            this.channel.close();
         }
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
