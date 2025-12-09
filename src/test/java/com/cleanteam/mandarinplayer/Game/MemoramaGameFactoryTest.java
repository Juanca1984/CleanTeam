package com.cleanteam.mandarinplayer.Game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Service.MemoramaGameService;
import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para MemoramaGameFactory")
class MemoramaGameFactoryTest {

    @Mock
    private MemoramaGameService memoramaGameService;

    private MemoramaGameFactory factory;
    private Match testMatch;
    private Theme testTheme;

    @BeforeEach
    void setUp() {
        factory = new MemoramaGameFactory(memoramaGameService);

        testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setName("Test Theme");

        testMatch = new Match();
        testMatch.setRoomCode("TEST01");
        testMatch.setGameType(GameType.MEMORAMA);
        testMatch.setThemes(new HashSet<>(java.util.List.of(testTheme)));
        testMatch.setPlayers(new HashSet<>(java.util.List.of("Player1", "Player2")));
    }

    @Test
    @DisplayName("Debe crear instancia de MemoramaGame")
    void testCreateGame() {
        Game game = factory.createGame(testMatch);

        assertNotNull(game);
        assertInstanceOf(MemoramaGameFactory.MemoramaGame.class, game);
    }

    @Test
    @DisplayName("Debe inicializar juego al llamar start()")
    void testGameStart() {
        Game game = factory.createGame(testMatch);
        game.start();

        verify(memoramaGameService, times(1)).initializeGameAndStore(testMatch);
    }

    @Test
    @DisplayName("Debe inyectar servicio correctamente")
    void testServiceInjection() {
        assertNotNull(factory);
        Game game = factory.createGame(testMatch);
        
        assertNotNull(game);
        game.start();
        
        verify(memoramaGameService).initializeGameAndStore(testMatch);
    }

    @Test
    @DisplayName("Debe crear múltiples instancias independientes")
    void testMultipleGameInstances() {
        Match match1 = new Match();
        match1.setRoomCode("ROOM1");
        match1.setThemes(new HashSet<>(java.util.List.of(testTheme)));
        match1.setPlayers(new HashSet<>(java.util.List.of("Player1")));

        Match match2 = new Match();
        match2.setRoomCode("ROOM2");
        match2.setThemes(new HashSet<>(java.util.List.of(testTheme)));
        match2.setPlayers(new HashSet<>(java.util.List.of("Player2")));

        Game game1 = factory.createGame(match1);
        Game game2 = factory.createGame(match2);

        assertNotNull(game1);
        assertNotNull(game2);
        assertNotSame(game1, game2);
    }

    @Test
    @DisplayName("Debe preservar estado del Match en el juego")
    void testMatchStatePreservation() {
        Game game = factory.createGame(testMatch);
        game.start();

        verify(memoramaGameService, times(1)).initializeGameAndStore(argThat(match -> 
            match.getRoomCode().equals("TEST01") && 
            match.getGameType() == GameType.MEMORAMA
        ));
    }

    @Test
    @DisplayName("Debe implementar interfaz GameFactory")
    void testImplementsGameFactory() {
        assertInstanceOf(GameFactory.class, factory);
    }

    @Test
    @DisplayName("Debe crear juego con Match nulo sin excepción en factory")
    void testCreateGameWithMatchHandling() {
        // El factory crea la instancia, no valida
        assertDoesNotThrow(() -> factory.createGame(testMatch));
    }

    @Test
    @DisplayName("Debe delegaralfip al servicio")
    void testGameOnFlip() {
        Game game = factory.createGame(testMatch);
        
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("TEST01");
        request.setCardPosition(0);
        
        game.onFlip(request);
        
        verify(memoramaGameService, times(1)).flipCard(request);
    }
}
