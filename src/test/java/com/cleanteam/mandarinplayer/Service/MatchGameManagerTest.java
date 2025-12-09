package com.cleanteam.mandarinplayer.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.DTO.GameStateResponse;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.MemoramaGameState;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;

import java.util.HashSet;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para MatchGameManager")
class MatchGameManagerTest {

    @Mock
    private MemoramaGameService memoramaGameService;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchGameManager matchGameManager;

    private Match testMatch;
    private MemoramaGameState testGameState;

    @BeforeEach
    void setUp() {
        Theme testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setName("Basic Words");

        testMatch = new Match();
        testMatch.setRoomCode("TEST01");
        testMatch.setThemes(new HashSet<>(List.of(testTheme)));
        testMatch.setPlayers(new HashSet<>(List.of("Player1", "Player2")));

        testGameState = new MemoramaGameState();
        testGameState.setRoomCode("TEST01");
        testGameState.setWaitingForFlip(true);
    }

    @Test
    @DisplayName("Debe crear juego si no existe")
    void testCreateOrGetGameForMatchCreatesNewGame() {
        when(memoramaGameService.getState("TEST01")).thenReturn(null);
        when(memoramaGameService.initializeGameAndStore(testMatch)).thenReturn(testGameState);

        MemoramaGameState result = matchGameManager.createOrGetGameForMatch(testMatch);

        assertNotNull(result);
        assertEquals("TEST01", result.getRoomCode());
        verify(memoramaGameService, times(1)).getState("TEST01");
        verify(memoramaGameService, times(1)).initializeGameAndStore(testMatch);
    }

    @Test
    @DisplayName("Debe retornar juego existente si ya existe")
    void testCreateOrGetGameForMatchReturnsExisting() {
        when(memoramaGameService.getState("TEST01")).thenReturn(testGameState);

        MemoramaGameState result = matchGameManager.createOrGetGameForMatch(testMatch);

        assertNotNull(result);
        assertEquals("TEST01", result.getRoomCode());
        verify(memoramaGameService, times(1)).getState("TEST01");
        verify(memoramaGameService, never()).initializeGameAndStore(testMatch);
    }

    @Test
    @DisplayName("Debe manejar flip de carta")
    void testHandleFlip() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("TEST01");
        request.setCardPosition(5);

        GameStateResponse response = new GameStateResponse();
        when(memoramaGameService.flipCard(request)).thenReturn(response);

        GameStateResponse result = matchGameManager.handleFlip(request);

        assertNotNull(result);
        verify(memoramaGameService, times(1)).flipCard(request);
    }

    @Test
    @DisplayName("Debe persistir snapshot")
    void testPersistSnapshot() {
        when(memoramaGameService.getState("TEST01")).thenReturn(testGameState);

        matchGameManager.persistSnapshot("TEST01");

        verify(memoramaGameService, times(1)).getState("TEST01");
    }
}
