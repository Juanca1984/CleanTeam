package com.cleanteam.mandarinplayer.WebSocket;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.DTO.GameStateResponse;
import com.cleanteam.mandarinplayer.Service.MatchStateManager;
import com.cleanteam.mandarinplayer.Service.MemoramaGameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchStateManager matchStateManager;

    public GameWsController(MatchStateManager matchStateManager,
                            SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.matchStateManager = matchStateManager;
    }

    @MessageMapping("/matches/flip")
    public void flip(@Payload FlipCardRequest req, StompHeaderAccessor accessor) {
        if (req == null || req.getRoomCode() == null) {
            return;
        }
        String sessionId = accessor != null ? accessor.getSessionId() : null;
        if (sessionId == null) {
            return;
        }

        try {
            // Delegar en MatchStateManager; éste publica /topic/matches/{room}/game y el lobby.
            matchStateManager.flip(req, accessor);
        } catch (Exception e) {
            String principal = accessor != null && accessor.getUser() != null ? accessor.getUser().getName() : null;
            if (principal != null) {
                messagingTemplate.convertAndSendToUser(principal, "/queue/errors", e.getMessage());
            }
        }
    }
}