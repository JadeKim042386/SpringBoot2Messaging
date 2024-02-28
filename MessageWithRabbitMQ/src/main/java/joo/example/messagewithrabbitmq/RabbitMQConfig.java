package joo.example.messagewithrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    static final String topicExchangeName = "spring-boot-topic-exchange";
    static final String directExchangeName = "spring-boot-direct-exchange";
    static final String fanoutExchangeName = "spring-boot-fanout-exchange";
    static final String headersExchangeName = "spring-boot-headers-exchange";

    static final String queueName = "spring-boot";

    /**
     * 큐 생성
     */
    @Bean
    Queue queue() {
        //durable이 true일 경우 디스크에 저장되며, false면 메모리에 저장
        //exclusive가 true일 경우 특정 연결에 대한 접근을 제한하고 해당 연결이 종료되면 자동으로 삭제
        //autoDelete가 true일 경우 모든 consumer와 disconnect된 queue를 자동 삭제
        return new Queue(queueName, false);
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
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter); //message listener 등록
        return container;
    }

    /**
     * Receiver는 POJO이기 때문에 MessageListenerAdapter로 wrapping해준다.
     */
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
