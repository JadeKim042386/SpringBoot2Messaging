# Publish/Subscribe

- producer: message를 전송하는 user application
- queue: message가 저장되는 buffer
- consumer: message를 받는 user application
- exchange
  - 한 쪽에서는 producer로부터 message를 받고 다른 쪽에서는 queue로 push
  - type: `direct`, `topic`, `headers`, `fanout`
  - direct exchange
    - routing key를 기반으로 메시지를 전달하는 방식
    - 메시지가 excahnge로 들어오면 메시지의 routing key를 확인하고 일치하는 queue로 라우팅
  - fanout exchange
    - 모든 queue로 메시지를 보내는 방식
    - 모든 queue에 메시지를 보내기때문에 routing key는 무시됨
  - topic exchange
    - routing key를 사용하지만 `direct` 타입과 달리 특정 값이 아닌 패턴으로 binding
  - headers exchange
    - 메시지의 헤더에 여러 속성 값을 사용하여 라우팅하는 방식
    - routing key는 무시되며 헤더의 값과 binding하여 일치하는 queue로 라우팅
- binding: exchange와 queue 사이의 relationship을 binding이라 부른다.

## About queue size

- 이용 가능한 모든 resource를 사용하기 때문에 Queue 개수는 내부적으로 제한되어있지 않다고 한다.
- 모든 worker들이 바쁘게 돌아간다면 Queue가 가득 차버릴 수 있다. 따라서 계속 지켜보며 더 많은 worker를 추가하거나 다른 전략을 가져야할 것이다.

## Fair dispatch vs Round-robin dispatching

- 기본적으로 RabbitMQ는 consumer에게 message를 순차적으로 전송한다. 따라서 평균적으로 모든 consumer들은 같은 개수의 message를 얻게된다. 이런 분산 message 방식을 `round-robin`이라 부른다.
  - 예를 들어 두 개의 worker들이 있고 모든 홀수 message들은 무거우며 짝수 message들은 가볍다고하자. 그러면 한 worker는 항상 바쁘게 돌아가고 다른 하나는 거의 동작하지 않을 것이다.
- Spring AMQP에서 default로 `Fair dispatch`가 설정되어있다. 그리고 `DEFAULT_PREFETCH_COUNT`는 250이다. (DEFAULT_PREFETCH_COUNT는 worker에게 한 번에 건네는 message 수)
- 만약 `DEFAULT_PREFETCH_COUNT`를 1로 설정한다면 round robin 방식으로 작동한다.

### Reference

- https://spring.io/guides/gs/messaging-rabbitmq
- https://www.rabbitmq.com/tutorials/tutorial-three-spring-amqp
- https://docs.spring.io/spring-amqp/reference/amqp/sending-messages.html
- https://stackoverflow.com/questions/22989833/rabbitmq-how-many-queues-can-rabbitmq-handle-on-a-single-server