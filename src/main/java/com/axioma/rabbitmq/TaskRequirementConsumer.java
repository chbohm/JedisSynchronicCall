package com.axioma.rabbitmq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class TaskRequirementConsumer {

   public TaskRequirementConsumer() {

      try {
         final RabbitMQHandler handler = RabbitMQHandler.getInstance();
         handler.queueDeclare(QueueNames.TASK_REQUIREMENT_QUEUE_NAME);
         Channel channel = handler.getChannel();
         System.out.println(" [*] Waiting for task messages. To exit press CTRL+C");

         QueueingConsumer consumer = new QueueingConsumer(channel);
         channel.basicConsume(QueueNames.TASK_REQUIREMENT_QUEUE_NAME, true, consumer);

         while (true) {

            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();

            String statusQueue = "requestName" + "_status";
            String resultsQueue = "requestName" + "_results";
            handler.queueDeclare(statusQueue);
            handler.queueDeclare(resultsQueue);

            handler.getChannel().basicPublish("", props.getReplyTo(), replyProps, "ChannelsCreated".getBytes());
            handler.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            Runnable r = new Runnable() {

               @Override
               public void run() {
                  Job job = new Job(statusQueue, resultsQueue);
                  job.process(message);
               }

            };

            Thread t = new Thread(r, message + "ProcessingThread");
            t.run();

         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

}
