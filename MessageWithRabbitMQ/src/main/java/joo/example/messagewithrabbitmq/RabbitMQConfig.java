package joo.example.messagewithrabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitMQConfig {
    static final String topicExchangeName = "spring-boot-topic-exchange";
    static final String directExchangeName = "spring-boot-direct-exchange";
    static final String fanoutExchangeName = "spring-boot-fanout-exchange";
    static final String headersExchangeName = "spring-boot-headers-exchange";

    static final String queueName = "spring-boot";

    static final String dlqExchangeName = topicExchangeName + ".dlx";
    static final String dlqQueueName = queueName + ".dlq";

    static final String parkingLotExchangeName = topicExchangeName + ".parking-lot";
    static final String parkingLotQueueName = queueName + ".parking-lot";

    /**
     * 큐 생성
     */
    @Bean
    Queue queue() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", dlqExchangeName);
        return new Queue(queueName, true, false, false, args);
    }

    /**
     * DLQ 생성
     */
    @Bean
    Queue dlqQueue() {
        return new Queue(dlqQueueName);
    }

    /**
     * Parking Lot Queue 생성
     */
    @Bean
    Queue parkingLotQueue() {
        return new Queue(parkingLotQueueName);
    }

    /**
     * TopicExchange 생성
     * - 패턴으로 routing key를 binding
     */
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName);
    }

    /**
     * DirectExchange 생성
     * - routing key를 사용한 binding
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(directExchangeName);
    }

    /**
     * fanoutExchange 생성
     * - 모든 queue로 메시지 전송
     */
    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(fanoutExchangeName);
    }

    /**
     * headersExchange 생성
     * - 헤더의 값을 사용한 binding
     */
    @Bean
    HeadersExchange headersExchange() {
        return new HeadersExchange(headersExchangeName);
    }

    /**
     * DLX 생성
     */
    @Bean
    TopicExchange dlqExchange() {
        return new TopicExchange(dlqExchangeName);
    }

    /**
     * ParkingLotExchange 생성
     */
    @Bean
    TopicExchange parkingLotExchange() {
        return new TopicExchange(parkingLotExchangeName);
    }

    /**
     * 큐와 TopicExchange 바인딩
     */
    @Bean
    Binding topicBinding(Queue queue, TopicExchange topicExchange) {
        //foo.bar로 시작하는 routing key와 같이 message를 보내는 것을 의미
        //foo.bar.* 패턴과 일치하는 queue로 라우팅
        return BindingBuilder.bind(queue).to(topicExchange).with("foo.bar.#");
    }

    /**
     * 큐와 DirectExchange 바인딩
     */
    @Bean
    Binding directBinding(Queue queue, DirectExchange directExchange) {
        //routing key와 같이 message를 보내는 것을 의미
        //foo.bar.direct와 일치하는 queue로 라우팅
        return BindingBuilder.bind(queue).to(directExchange).with("direct");
    }

    /**
     * 큐와 FanoutExchange 바인딩
     */
    @Bean
    Binding fanoutBinding(Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    /**
     * 큐와 HeadersExchange 바인딩
     */
    @Bean
    Binding headersBinding(Queue queue, HeadersExchange headersExchange) {
        return BindingBuilder.bind(queue).to(headersExchange).where("headers").exists();
    }

    @Bean
    Binding dlqBinding(Queue dlqQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(dlqQueue).to(dlqExchange).with("foo.bar.#");
    }

    @Bean
    Binding parkingLotBinding(Queue parkingLotQueue, TopicExchange parkingLotExchange) {
        return BindingBuilder.bind(parkingLotQueue).to(parkingLotExchange).with("foo.bar.#");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topicExchangeName);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        rabbitTemplate.setMandatory(true);
        // 메시지가 브로커에 도착했지만 지정된 큐로 라우팅되지 못한 경우
        rabbitTemplate.setReturnsCallback((returnedMessage) -> {
            log.info("routingKey: {}, replyText: {}", returnedMessage.getRoutingKey(), returnedMessage.getReplyText());
        });
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
