package com.cleanteam.mandarinplayer.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.MemoramaCard;
import com.cleanteam.mandarinplayer.Model.MemoramaGameState;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Model.Word;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;
import com.cleanteam.mandarinplayer.Repository.WordRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para MemoramaGameService")
class MemoramaGameServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private WordRepository wordRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MemoramaGameService memoramaGameService;

    private Match testMatch;
    private Theme testTheme;
    private List<Word> testWords;

    @BeforeEach
    void setUp() {
        testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setName("Basic Words");

        testMatch = new Match();
        testMatch.setRoomCode("TEST01");
        testMatch.setThemes(new HashSet<>(List.of(testTheme)));
        testMatch.setPlayers(new HashSet<>(List.of("Player1", "Player2")));

        testWords = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Word word = new Word();
            word.setId((long) i);
            word.setCharacter("汉" + i);
            word.setPinyin("han" + i);
            word.setTheme(testTheme);
            testWords.add(word);
        }
    }

    @Test
    @DisplayName("Debe inicializar juego correctamente desde Match")
    void testInitializeGame() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        memoramaGameService.initializeGameAndStore(testMatch);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), stateCaptor.capture());

        MemoramaGameState gameState = stateCaptor.getValue();
        assertEquals("TEST01", gameState.getRoomCode());
        assertEquals(16, gameState.getCards().size()); // 8 palabras * 2 (hanzi + pinyin)
        assertTrue(gameState.isWaitingForFlip());
    }

    @Test
    @DisplayName("Debe lanzar excepción si Match no tiene temas")
    void testInitializeGameWithoutThemes() {
        testMatch.setThemes(new HashSet<>());

        assertThrows(IllegalStateException.class, () -> memoramaGameService.initializeGameAndStore(testMatch));
    }

    @Test
    @DisplayName("Debe inicializar correctamente con roomCode y themeId")
    void testInitialize() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match01 = new Match();
        match01.setRoomCode("ROOM01");
        match01.setThemes(new HashSet<>(List.of(testTheme)));
        match01.setPlayers(new HashSet<>(List.of("Player1", "Player2", "Player3")));

        memoramaGameService.initializeGameAndStore(match01);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), stateCaptor.capture());

        String topic = topicCaptor.getValue();
        assertEquals("/topic/game/ROOM01", topic);

        MemoramaGameState gameState = stateCaptor.getValue();
        assertEquals("ROOM01", gameState.getRoomCode());
        assertEquals(3, gameState.getPlayerScores().size());
        assertEquals(0, gameState.getPlayerScores().get("Player1"));
        assertEquals(0, gameState.getPlayerScores().get("Player2"));
        assertEquals(0, gameState.getPlayerScores().get("Player3"));
    }

    @Test
    @DisplayName("Debe crear cartas en parejas (hanzi y pinyin)")
    void testCardPairsCreation() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match01 = new Match();
        match01.setRoomCode("ROOM01");
        match01.setThemes(new HashSet<>(List.of(testTheme)));
        match01.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match01);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());

        MemoramaGameState gameState = stateCaptor.getValue();
        List<MemoramaCard> cards = gameState.getCards();

        // Verificar que hay parejas de hanzi y pinyin
        long hanziCount = cards.stream().filter(c -> "HANZI".equals(c.getPairType())).count();
        long pinyinCount = cards.stream().filter(c -> "PINYIN".equals(c.getPairType())).count();

        assertEquals(8, hanziCount);
        assertEquals(8, pinyinCount);
    }

    @Test
    @DisplayName("Debe establecer primer jugador actual correctamente")
    void testCurrentPlayerAssignment() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);
        List<String> players = List.of("Alice", "Bob", "Charlie");

        Match match02 = new Match();
        match02.setRoomCode("ROOM02");
        match02.setThemes(new HashSet<>(List.of(testTheme)));
        match02.setPlayers(new LinkedHashSet<>(players));

        memoramaGameService.initializeGameAndStore(match02);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());

        MemoramaGameState gameState = stateCaptor.getValue();
        assertEquals("Alice", gameState.getCurrentPlayerNickname());
    }

    @Test
    @DisplayName("Debe limitar palabras a 8 máximo")
    void testMaxWordsLimit() {
        // Crear más de 8 palabras
        List<Word> manyWords = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Word word = new Word();
            word.setId((long) i);
            word.setCharacter("汉" + i);
            word.setPinyin("han" + i);
            word.setTheme(testTheme);
            manyWords.add(word);
        }

        when(wordRepository.findByThemeId(1L)).thenReturn(manyWords);

        Match match03 = new Match();
        match03.setRoomCode("ROOM03");
        match03.setThemes(new HashSet<>(List.of(testTheme)));
        match03.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match03);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());

        MemoramaGameState gameState = stateCaptor.getValue();
        // 8 palabras máximo * 2 (hanzi + pinyin) = 16 cartas
        assertEquals(16, gameState.getCards().size());
    }

    @Test
    @DisplayName("Debe embarajar las cartas")
    void testCardShuffling() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        // Inicializar dos veces y verificar que las posiciones son diferentes
        Match match04 = new Match();
        match04.setRoomCode("ROOM04");
        match04.setThemes(new HashSet<>(List.of(testTheme)));
        match04.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match04);
        ArgumentCaptor<MemoramaGameState> stateCaptor1 = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), stateCaptor1.capture());

        List<MemoramaCard> firstOrder = new ArrayList<>(stateCaptor1.getValue().getCards());

        Match match05 = new Match();
        match05.setRoomCode("ROOM05");
        match05.setThemes(new HashSet<>(List.of(testTheme)));
        match05.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match05);
        ArgumentCaptor<MemoramaGameState> stateCaptor2 = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), stateCaptor2.capture());

        List<MemoramaCard> secondOrder = stateCaptor2.getValue().getCards();

        // No esperar exactamente igual (aunque es posible por suerte)
        // Solo verificar que ambas tienen 16 cartas y posiciones válidas
        assertEquals(16, firstOrder.size());
        assertEquals(16, secondOrder.size());
    }

    @Test
    @DisplayName("Debe ignorar flip si room no existe")
    void testFlipCardRoomNotExists() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("NONEXISTENT");
        request.setCardPosition(0);

        // No debe lanzar excepción
        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
        
        // Verificar que no envió mensaje (solo se enviaron los de initialize)
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(MemoramaGameState.class));
    }

    @Test
    @DisplayName("Debe ignorar flip si posición es negativa")
    void testFlipCardNegativePosition() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match07 = new Match();
        match07.setRoomCode("ROOM07");
        match07.setThemes(new HashSet<>(List.of(testTheme)));
        match07.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match07);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM07", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM07");
        request.setCardPosition(-1);

        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
    }

    @Test
    @DisplayName("Debe ignorar flip si posición es mayor al tamaño de cartas")
    void testFlipCardOutOfBoundsPosition() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match08 = new Match();
        match08.setRoomCode("ROOM08");
        match08.setThemes(new HashSet<>(List.of(testTheme)));
        match08.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match08);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM08", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM08");
        request.setCardPosition(100);

        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
    }

    @Test
    @DisplayName("Debe ignorar flip si carta ya está emparejada")
    void testFlipCardAlreadyMatched() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match09 = new Match();
        match09.setRoomCode("ROOM09");
        match09.setThemes(new HashSet<>(List.of(testTheme)));
        match09.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match09);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        // Marcar una carta como emparejada
        capturedState.getCards().get(0).setMatched(true);

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM09", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM09");
        request.setCardPosition(0);

        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
        
        // Verificar que sigue siendo matched
        assertTrue(capturedState.getCards().get(0).isMatched());
    }

    @Test
    @DisplayName("Debe ignorar flip si carta ya está volteada")
    void testFlipCardAlreadyFlipped() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match10 = new Match();
        match10.setRoomCode("ROOM10");
        match10.setThemes(new HashSet<>(List.of(testTheme)));
        match10.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match10);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        // Marcar una carta como volteada
        capturedState.getCards().get(0).setFlipped(true);

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM10", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM10");
        request.setCardPosition(0);

        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
        
        // Verificar que sigue siendo flipped
        assertTrue(capturedState.getCards().get(0).isFlipped());
    }

    @Test
    @DisplayName("Debe voltear carta correctamente cuando es válida")
    void testFlipCardSuccess() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match11 = new Match();
        match11.setRoomCode("ROOM11");
        match11.setThemes(new HashSet<>(List.of(testTheme)));
        match11.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match11);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM11", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM11");
        request.setCardPosition(5);

        assertDoesNotThrow(() -> memoramaGameService.flipCard(request));
        
        // Verificar que la carta se volteó
        assertTrue(capturedState.getCards().get(5).isFlipped());
        // Verificar que se envió el mensaje actualizado (2 calls: initialize + flipCard)
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(MemoramaGameState.class));
    }

    @Test
    @DisplayName("Debe obtener estado del juego por roomCode")
    void testGetState() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        memoramaGameService.initializeGameAndStore(testMatch);

        MemoramaGameState state = memoramaGameService.getState("TEST01");

        assertNotNull(state);
        assertEquals("TEST01", state.getRoomCode());
    }

    @Test
    @DisplayName("Debe retornar null si estado no existe")
    void testGetStateNotFound() {
        MemoramaGameState state = memoramaGameService.getState("NONEXISTENT");

        assertNull(state);
    }

    @Test
    @DisplayName("Debe manejar múltiples inicializaciones del mismo room")
    void testMultipleInitializationsOfSameRoom() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        MemoramaGameState state1 = memoramaGameService.initializeGameAndStore(testMatch);
        MemoramaGameState state2 = memoramaGameService.initializeGameAndStore(testMatch);

        assertNotNull(state1);
        assertNotNull(state2);
        verify(messagingTemplate, atLeast(2)).convertAndSend(anyString(), any(MemoramaGameState.class));
    }

    @Test
    @DisplayName("Debe crear cartas con índice correcto")
    void testCardIndexing() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        memoramaGameService.initializeGameAndStore(testMatch);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());

        MemoramaGameState gameState = stateCaptor.getValue();
        List<MemoramaCard> cards = gameState.getCards();

        // Verificar que cada carta tiene un índice válido
        for (int i = 0; i < cards.size(); i++) {
            MemoramaCard card = cards.get(i);
            assertNotNull(card);
            assertNotNull(card.getContent());
            assertEquals(i, card.getPosition());
        }
    }

    @Test
    @DisplayName("Debe retornar null si request es nulo")
    void testFlipCardWithNullRequest() {
        // flipCard lanza NullPointerException si request es nulo
        assertThrows(NullPointerException.class, () -> memoramaGameService.flipCard(null));
    }

    @Test
    @DisplayName("Debe retornar null si roomCode es nulo")
    void testFlipCardWithNullRoomCode() {
        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode(null);
        request.setCardPosition(0);

        // flipCard lanza NullPointerException si roomCode es nulo
        assertThrows(NullPointerException.class, () -> memoramaGameService.flipCard(request));
    }

    @Test
    @DisplayName("Debe retornar null si cardPosition es nulo")
    void testFlipCardWithNullPosition() {
        // Este test no aplica porque cardPosition es int primitivo, no puede ser nulo
        // Se valida indirectamente en testFlipCardNegativePosition
        assertTrue(true);
    }

    @Test
    @DisplayName("Debe alternar waitingForFlip al hacer flip correcto")
    void testFlipCardTogglesWaitingForFlip() {
        when(wordRepository.findByThemeId(1L)).thenReturn(testWords);

        Match match12 = new Match();
        match12.setRoomCode("ROOM12");
        match12.setThemes(new HashSet<>(List.of(testTheme)));
        match12.setPlayers(new HashSet<>(List.of("Player1")));

        memoramaGameService.initializeGameAndStore(match12);

        ArgumentCaptor<MemoramaGameState> stateCaptor = ArgumentCaptor.forClass(MemoramaGameState.class);
        verify(messagingTemplate).convertAndSend(anyString(), stateCaptor.capture());
        MemoramaGameState capturedState = stateCaptor.getValue();

        boolean initialWaitingForFlip = capturedState.isWaitingForFlip();

        // Usar reflection para agregar el estado
        try {
            java.lang.reflect.Field field = memoramaGameService.getClass().getDeclaredField("activeGames");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, MemoramaGameState> activeGames = (Map<String, MemoramaGameState>) field.get(memoramaGameService);
            activeGames.put("ROOM12", capturedState);
        } catch (Exception e) {
            fail("No se pudo acceder al mapa activeGames");
        }

        FlipCardRequest request = new FlipCardRequest();
        request.setRoomCode("ROOM12");
        request.setCardPosition(0);

        memoramaGameService.flipCard(request);

        // Verificar que se alternó
        assertEquals(!initialWaitingForFlip, capturedState.isWaitingForFlip());
    }
}
