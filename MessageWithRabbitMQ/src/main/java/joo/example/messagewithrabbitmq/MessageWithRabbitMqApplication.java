package joo.example.messagewithrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;

@SpringBootApplication
public class MessageWithRabbitMqApplication {


    public static void main(String[] args) {
        SpringApplication.run(MessageWithRabbitMqApplication.class, args).close();
    }

}
