package joo.example.messagesseemitter.api;

import joo.example.messagesseemitter.dto.MemberPrincipal;
import joo.example.messagesseemitter.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/v1/alarm")
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmService alarmService;


    /**
     * SSE 연결 요청
     */
    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        return ResponseEntity.ok(alarmService.connectAlarm(memberPrincipal.username()));
    }

    /**
     * 메시지 전송
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendAlarm(@RequestParam String receiverName, @RequestParam String content) {
        alarmService.send(receiverName, content);
        return ResponseEntity.ok(null);
    }
}
