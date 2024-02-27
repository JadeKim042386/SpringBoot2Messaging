package joo.example.messagewithrabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class Receiver {
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        log.info("Received <" + message + ">");
        latch.countDown();
    }

    public void receiveMessage(byte[] message) {
        log.info("Received <" + new String(message, StandardCharsets.UTF_8) + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
