package com.axioma.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TaskRequirementProducer {


   public static void main(final String[] args) throws IOException {
      RabbitMQHandler handler = RabbitMQHandler.getInstance();


      handler.dispose();
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = null;
      Channel channel = null;
      try {
         connection = factory.newConnection();
         channel = connection.createChannel();
         channel.queueDeclare(QueueNames.TASK_REQUIREMENT_QUEUE_NAME, false, false, false, null);
         String message = "Hello Zaca!";
         channel.basicPublish("", QueueNames.TASK_REQUIREMENT_QUEUE_NAME, null, message.getBytes());
         System.out.println(" [x] Sent '" + message + "'");
      } catch (Exception e) {
         e.printStackTrace();
      } finally {

         channel.close();
         connection.close();
      }
   }

}
