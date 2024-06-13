package joo.example.messagewithrabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) {
        log.info("Sending message...");
        String message = "Hello from RabbitMQ!";
        rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, "foo.bar.baz", message);
//        rabbitTemplate.convertAndSend(RabbitMQConfig.directExchangeName, "direct", message);
//        rabbitTemplate.convertAndSend(RabbitMQConfig.fanoutExchangeName, "", message);

//        Message messageWithHeader = MessageBuilder.withBody(message.getBytes())
//                .setHeader("headers", "h")
//                .build();
//        rabbitTemplate.convertAndSend(RabbitMQConfig.headersExchangeName, "", messageWithHeader);
//        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}
