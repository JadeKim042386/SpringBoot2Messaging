package joo.example.messagewebsockethandler.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joo.example.messagewebsockethandler.dto.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    /**
     * 웹 소켓 연결
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String principalId = getPrincipalId(session);
        //유저의 id를 key값으로하여 session을 저장한다.
        sessions.put(principalId, session);
    }

    /**
     * 양방향 데이터 통신
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readValue(message.getPayload(), JsonNode.class);
        //현재 session의 principal은 sender에 해당한다.
        //message에는 receiver의 id가 포함되어야하며 이를 통해 receiver의 session을 찾아 메시지를 전송한다.
        Optional.of(sessions.get(jsonNode.get("receiverId").textValue()))
                .filter(WebSocketSession::isOpen)
                .ifPresent(s -> {
                    try {
                        s.sendMessage(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 소켓 통신 에러
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    /**
     * 소켓 연결 종료
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(getPrincipalId(session));
    }

    private String getPrincipalId(WebSocketSession session) {
        //session으로부터 principal을 가져와 현재 인증된 유저의 id를 얻는다. 없다면 예외가 발생한다.
        return Optional.ofNullable((Authentication) session.getPrincipal())
                .map(a -> String.valueOf(((MemberPrincipal) a.getPrincipal()).id()))
                .orElseThrow(() -> new RuntimeException("current not signin"));
    }
}
