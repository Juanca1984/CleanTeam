package com.cleanteam.mandarinplayer.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.DTO.LobbyEvent;
import com.cleanteam.mandarinplayer.DTO.StartMatchRequest;
import com.cleanteam.mandarinplayer.Game.Game;
import com.cleanteam.mandarinplayer.Game.GameFactory;
import com.cleanteam.mandarinplayer.Game.GameFactoryRegistry;
import com.cleanteam.mandarinplayer.Game.GameType;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.MatchStatus;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para MatchStateManager")
class MatchStateManagerTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameFactoryRegistry gameFactoryRegistry;

    @Mock
    private GameFactory gameFactory;

    @Mock
    private Game game;

    @InjectMocks
    private MatchStateManager matchStateManager;

    private Match testMatch;

    @BeforeEach
    void setUp() {
        testMatch = new Match();
        testMatch.setStatus(MatchStatus.CONFIGURING);
        testMatch.setRoomCode("TEST01");
        testMatch.setGameType(GameType.MEMORAMA);
        testMatch.setPlayers(new HashSet<>(java.util.List.of("Player1", "Player2")));
    }

    @Test
    @DisplayName("Debe registrar conexión de jugador")
    void testOnPlayerConnected() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LobbyEvent> eventCaptor = ArgumentCaptor.forClass(LobbyEvent.class);
        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), eventCaptor.capture());

        assertEquals("/topic/matches/TEST01", topicCaptor.getValue());
        LobbyEvent event = eventCaptor.getValue();
        assertEquals("TEST01", event.getRoomCode());
        assertEquals(1, event.getPlayersCount());
    }

    @Test
    @DisplayName("Debe registrar múltiples conexiones en la misma sala")
    void testMultiplePlayersConnected() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");
        matchStateManager.onPlayerConnected("TEST01", "session2", "Player2");

        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(LobbyEvent.class));
    }

    @Test
    @DisplayName("Debe desconectar jugador correctamente")
    void testOnPlayerDisconnected() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");
        matchStateManager.onPlayerDisconnected("TEST01", "session1");

        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(LobbyEvent.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si sala no existe al desconectar")
    void testOnPlayerDisconnectedNonExistent() {
        when(matchRepository.findByRoomCode("INVALID")).thenReturn(Optional.empty());
        
        // Debe lanzar excepción porque la sala no existe
        assertThrows(IllegalArgumentException.class, () -> matchStateManager.onPlayerDisconnected("INVALID", "session1"));
    }

    @Test
    @DisplayName("Debe iniciar partida correctamente")
    void testStartMatch() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("TEST01");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        when(gameFactoryRegistry.getFactory(GameType.MEMORAMA)).thenReturn(gameFactory);
        when(gameFactory.createGame(testMatch)).thenReturn(game);
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        Match result = matchStateManager.start(request);

        assertNotNull(result);
        assertEquals(MatchStatus.IN_PROGRESS, testMatch.getStatus());
        verify(game, times(1)).start();
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si partida no existe")
    void testStartMatchNotFound() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("INVALID");

        when(matchRepository.findByRoomCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> matchStateManager.start(request));
    }

    @Test
    @DisplayName("Debe lanzar excepción si no hay jugadores en la partida")
    void testStartMatchNoPlayers() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("TEST01");
        testMatch.setPlayers(new HashSet<>()); // Sin jugadores

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        assertThrows(IllegalStateException.class, () -> matchStateManager.start(request));
    }

    @Test
    @DisplayName("Debe publicar lobby con estado correcto")
    void testPublishLobbyByRoom() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");

        ArgumentCaptor<LobbyEvent> eventCaptor = ArgumentCaptor.forClass(LobbyEvent.class);
        verify(messagingTemplate).convertAndSend(anyString(), eventCaptor.capture());

        LobbyEvent event = eventCaptor.getValue();
        assertEquals("TEST01", event.getRoomCode());
        assertEquals(MatchStatus.CONFIGURING.name(), event.getStatus());
    }

    @Test
    @DisplayName("Debe actualizar estado de partida al iniciar")
    void testStartMatchStatusUpdate() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("TEST01");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        when(gameFactoryRegistry.getFactory(GameType.MEMORAMA)).thenReturn(gameFactory);
        when(gameFactory.createGame(testMatch)).thenReturn(game);
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            return m;
        });

        matchStateManager.start(request);

        assertEquals(MatchStatus.IN_PROGRESS, testMatch.getStatus());
    }

    @Test
    @DisplayName("Debe usar GameFactoryRegistry para crear el juego")
    void testGameFactoryRegistryUsage() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("TEST01");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        when(gameFactoryRegistry.getFactory(GameType.MEMORAMA)).thenReturn(gameFactory);
        when(gameFactory.createGame(testMatch)).thenReturn(game);
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        matchStateManager.start(request);

        verify(gameFactoryRegistry, times(1)).getFactory(GameType.MEMORAMA);
        verify(gameFactory, times(1)).createGame(testMatch);
    }

    @Test
    @DisplayName("Debe publicar lobby después de iniciar partida")
    void testPublishAfterStart() {
        StartMatchRequest request = new StartMatchRequest();
        request.setRoomCode("TEST01");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        when(gameFactoryRegistry.getFactory(GameType.MEMORAMA)).thenReturn(gameFactory);
        when(gameFactory.createGame(testMatch)).thenReturn(game);
        when(matchRepository.save(any(Match.class))).thenReturn(testMatch);

        matchStateManager.start(request);

        ArgumentCaptor<LobbyEvent> eventCaptor = ArgumentCaptor.forClass(LobbyEvent.class);
        verify(messagingTemplate).convertAndSend(anyString(), eventCaptor.capture());

        LobbyEvent event = eventCaptor.getValue();
        assertEquals(MatchStatus.IN_PROGRESS.name(), event.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción si flip request es null")
    void testFlipWithNullRequest() {
        assertThrows(IllegalArgumentException.class, 
            () -> matchStateManager.flip(null, null));
    }

    @Test
    @DisplayName("Debe lanzar excepción si roomCode es null")
    void testFlipWithNullRoomCode() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode(null);

        assertThrows(IllegalArgumentException.class,
            () -> matchStateManager.flip(request, null));
    }

    @Test
    @DisplayName("Debe lanzar excepción si sessionId es null")
    void testFlipWithoutSessionId() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("TEST01");

        assertThrows(IllegalArgumentException.class,
            () -> matchStateManager.flip(request, null));
    }

    @Test
    @DisplayName("Debe eliminar juego al terminar match")
    void testEndMatch() {
        matchStateManager.endMatch("TEST01");
        
        // El método solo remueve del mapa, no lanza excepción
        assertDoesNotThrow(() -> matchStateManager.endMatch("TEST01"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si room no existe en onPlayerDisconnected")
    void testOnPlayerDisconnectedRoomNotFound() {
        when(matchRepository.findByRoomCode("NONEXISTENT")).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, 
            () -> matchStateManager.onPlayerDisconnected("NONEXISTENT", "session1"));
    }

    @Test
    @DisplayName("Debe registrar múltiples desconexiones en la misma sala")
    void testMultiplePlayersDisconnected() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");
        matchStateManager.onPlayerConnected("TEST01", "session2", "Player2");
        matchStateManager.onPlayerDisconnected("TEST01", "session1");
        matchStateManager.onPlayerDisconnected("TEST01", "session2");

        verify(messagingTemplate, atLeast(4)).convertAndSend(anyString(), any(LobbyEvent.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si resolver juego falla por juego no disponible")
    void testFlipWithGameNotResolved() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("TEST01");
        request.setCardPosition(0);

        org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = 
            mock(org.springframework.messaging.simp.stomp.StompHeaderAccessor.class);
        when(accessor.getSessionId()).thenReturn("session1");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        testMatch.setGame(null); // No hay juego disponible

        // Debe lanzar IllegalStateException
        assertThrows(IllegalStateException.class, 
            () -> matchStateManager.flip(request, accessor));
    }

    @Test
    @DisplayName("Debe publicar estado del juego después de flip")
    void testFlipPublishesGameState() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("TEST01");
        request.setCardPosition(0);

        org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = 
            mock(org.springframework.messaging.simp.stomp.StompHeaderAccessor.class);
        when(accessor.getSessionId()).thenReturn("session1");

        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));
        testMatch.setGame(game);

        assertDoesNotThrow(() -> matchStateManager.flip(request, accessor));
    }

    @Test
    @DisplayName("Debe contar correctamente conexiones activas en publishLobby")
    void testPublishLobbyCountsConnections() {
        when(matchRepository.findByRoomCode("TEST01")).thenReturn(Optional.of(testMatch));

        matchStateManager.onPlayerConnected("TEST01", "session1", "Player1");
        matchStateManager.onPlayerConnected("TEST01", "session2", "Player2");

        ArgumentCaptor<LobbyEvent> eventCaptor = ArgumentCaptor.forClass(LobbyEvent.class);
        verify(messagingTemplate, atLeast(2)).convertAndSend(anyString(), eventCaptor.capture());

        // Verificar que el evento tiene 2 conexiones
        LobbyEvent event = eventCaptor.getValue();
        assertEquals(2, event.getPlayersCount());
    }

    @Test
    @DisplayName("Debe validar que flip request necesita roomCode")
    void testFlipValidatesRoomCode() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode(null);

        org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor = 
            mock(org.springframework.messaging.simp.stomp.StompHeaderAccessor.class);

        assertThrows(IllegalArgumentException.class, 
            () -> matchStateManager.flip(request, accessor));
    }
}
