package joo.example.messagesseemitter.service;

import joo.example.messagesseemitter.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private static final Long SSE_TIMEOUT = 60L * 60 * 1000; // 1시간
    private static final String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;

    /**
     * receiverName의 emitter를 찾아 메시지 전송
     */
    public void send(String receiverName, String content) {
        emitterRepository
                .get(receiverName)
                .ifPresentOrElse(
                        sseEmitter -> {
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id(receiverName)
                                        .name(ALARM_NAME)
                                        .data(content));
                            } catch (IOException e) {
                                emitterRepository.delete(receiverName);
                                throw new RuntimeException();
                            }
                        },
                        () -> log.info("Emitter를 찾을 수 없습니다."));
    }

    /**
     * SseEmitter 연결
     */
    public SseEmitter connectAlarm(String username) {
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);
        emitterRepository.save(username, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(username));
        sseEmitter.onTimeout(() -> emitterRepository.delete(username));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return sseEmitter;
    }
}
