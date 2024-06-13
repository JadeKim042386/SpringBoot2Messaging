package joo.example.messagewithrabbitmq;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "#{queue.name}")
    public void consume(Channel channel, Message message, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("consume from normal queue");
        log.info("message: {}", message);
//        channel.basicAck(tag, false);
        channel.basicReject(tag, false);
    }

    @RabbitListener(queues = "#{dlqQueue.name}")
    public void dlqConsume(Channel channel, Message message, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("consume from dead letter queue");
        log.info("message: {}", message);
        String HEADER_X_RETRIES_COUNT = "x-retries-count";
        Integer retriesCnt = (Integer) message.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);
        if (retriesCnt == null) retriesCnt = 1;
        else retriesCnt++;
        if (retriesCnt > 3) {
            log.info("Discarding message");
            rabbitTemplate.send(RabbitMQConfig.parkingLotExchangeName,
                    message.getMessageProperties().getReceivedRoutingKey(), message);
            channel.basicAck(tag, false);
            return;
        }
        log.info("Retrying message for the {} time", retriesCnt);
        message.getMessageProperties().getHeaders().put(HEADER_X_RETRIES_COUNT, retriesCnt);
        rabbitTemplate.send(RabbitMQConfig.topicExchangeName, message.getMessageProperties().getReceivedRoutingKey(), message);
        channel.basicAck(tag, false);
    }

    @RabbitListener(queues = "#{parkingLotQueue.name}")
    public void parkingLotConsume(Channel channel, Message message, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received message in parking lot queue");
        // Save to DB or send a notification.
        channel.basicAck(tag, false);
    }
}
