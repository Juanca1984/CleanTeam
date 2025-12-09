// java
package com.cleanteam.mandarinplayer.WebSocket;

import com.cleanteam.mandarinplayer.Service.MatchStateManager;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventsListener {

    private final MatchStateManager matchStateManager;

    public WebSocketEventsListener(MatchStateManager matchStateManager) {
        this.matchStateManager = matchStateManager;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        Message<?> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        String sessionId = accessor.getSessionId();
        String roomCode = accessor.getFirstNativeHeader("roomCode");
        String nickname = accessor.getFirstNativeHeader("nickname");

        // Persistir en atributos de sesión para recuperar en DISCONNECT
        if (roomCode != null) {
            accessor.getSessionAttributes().put("roomCode", roomCode);
        }
        if (nickname != null) {
            accessor.getSessionAttributes().put("nickname", nickname);
        }

        if (roomCode != null && nickname != null && sessionId != null) {
            matchStateManager.onPlayerConnected(roomCode, sessionId, nickname);
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Object roomAttr = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("roomCode")
                : null;

        if (roomAttr instanceof String roomCode && sessionId != null) {
            matchStateManager.onPlayerDisconnected(roomCode, sessionId);
        }
    }
}