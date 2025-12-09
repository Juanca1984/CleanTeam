package com.cleanteam.mandarinplayer.Service;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.DTO.GameStateResponse;
import com.cleanteam.mandarinplayer.DTO.LobbyEvent;
import com.cleanteam.mandarinplayer.DTO.StartMatchRequest;
import com.cleanteam.mandarinplayer.Game.Game;
import com.cleanteam.mandarinplayer.Game.GameFactoryRegistry;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.MatchStatus;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchStateManager {

    private final MatchRepository matchRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameFactoryRegistry gameFactoryRegistry;

    // Contador de conexiones por roomCode basado en sessionIds
    private final ConcurrentHashMap<String, Set<String>> roomConnections = new ConcurrentHashMap<>();

    // Mapa en memoria para mantener instancias activas de Game por roomCode
    private final ConcurrentHashMap<String, Game> activeGames = new ConcurrentHashMap<>();

    public MatchStateManager(MatchRepository matchRepository,
                             SimpMessagingTemplate messagingTemplate,
                             GameFactoryRegistry gameFactoryRegistry) {
        this.matchRepository = matchRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameFactoryRegistry = gameFactoryRegistry;
    }

    // Evento desde WebSocket: incrementa contador y publica lobby
    @Transactional
    public void onPlayerConnected(String roomCode, String sessionId, String nickname) {
        // Mantener el contador de conexiones por sesión
        roomConnections.computeIfAbsent(roomCode, rc -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        Match match = matchRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        if (nickname != null && !nickname.isBlank()) {
            match.getPlayers().add(nickname);
            matchRepository.save(match);
        }

        publishLobbyByRoom(roomCode);
    }

    // Evento desde WebSocket: decrementa contador y publica lobby
    public void onPlayerDisconnected(String roomCode, String sessionId) {
        Set<String> sessions = roomConnections.get(roomCode);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                roomConnections.remove(roomCode);
            }
        }
        publishLobbyByRoom(roomCode);
    }

    @Transactional
    public Match start(StartMatchRequest req) {
        Match match = matchRepository.findByRoomCode(req.getRoomCode())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        if (match.getPlayers().isEmpty()) {
            throw new IllegalStateException("At least one player required");
        }
        match.setStatus(MatchStatus.IN_PROGRESS);

        Game game = gameFactoryRegistry.getFactory(match.getGameType()).createGame(match);
        game.start();
        match.setGame(game);

        Match saved = matchRepository.save(match);

        // Guardar la instancia en memoria para futuras llamadas (flip, etc.)
        activeGames.put(saved.getRoomCode(), game);

        publishLobby(saved);
        return saved;
    }

    public GameStateResponse flip(FlipCardRequest req, StompHeaderAccessor accessor) {
        if (req == null || req.getRoomCode() == null) {
            throw new IllegalArgumentException("Invalid flip request");
        }
        String roomCode = req.getRoomCode();
        String sessionId = accessor != null ? accessor.getSessionId() : null;
        if (sessionId == null) {
            throw new IllegalArgumentException("No session id");
        }

        Match match = matchRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Recuperar la instancia desde memoria (o fallback a match.getGame())
        Game game = resolveGame(roomCode, match);

        GameStateResponse response = null;

        if (game != null) {
            try {
                Method m = game.getClass().getMethod("onFlip", FlipCardRequest.class);
                Object ret = m.invoke(game, req);
                if (ret instanceof GameStateResponse) {
                    response = (GameStateResponse) ret;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException("Error invoking game.onFlip", e);
            }
        }

        // Publicar estado del juego en el canal del match
        messagingTemplate.convertAndSend("/topic/matches/" + roomCode + "/game", response);

        publishLobby(match);

        return response;
    }

    // Resuelve la instancia de Game: primero mapa en memoria, luego fallback a match.getGame()
    private Game resolveGame(String roomCode, Match match) {
        Game game = activeGames.get(roomCode);
        if (game != null) return game;
        // fallback simple: si match tiene referencia (posible si aún está en memoria)
        game = match.getGame();
        if (game != null) {
            activeGames.put(roomCode, game);
            return game;
        }
        throw new IllegalStateException("Game instance not available for room " + roomCode);
    }

    // Opcional: limpiar instancia cuando termine el match
    public void endMatch(String roomCode) {
        activeGames.remove(roomCode);
    }

    private void publishLobby(Match match) {
        int connected = roomConnections.getOrDefault(match.getRoomCode(), ConcurrentHashMap.newKeySet()).size();
        LobbyEvent event = new LobbyEvent(
                match.getRoomCode(),
                connected,
                match.getStatus().name()
        );
        messagingTemplate.convertAndSend("/topic/matches/" + match.getRoomCode(), event);
    }

    private void publishLobbyByRoom(String roomCode) {
        Match match = matchRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        publishLobby(match);
    }
}