// java
package com.cleanteam.mandarinplayer.Service;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.DTO.GameStateResponse;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.MemoramaCard;
import com.cleanteam.mandarinplayer.Model.MemoramaGameState;
import com.cleanteam.mandarinplayer.Model.Word;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;
import com.cleanteam.mandarinplayer.Repository.WordRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoramaGameService {

    private final Map<String, MemoramaGameState> activeGames = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final WordRepository wordRepository;
    private final MatchRepository matchRepository;

    public MemoramaGameService(SimpMessagingTemplate messagingTemplate,
                               WordRepository wordRepository,
                               MatchRepository matchRepository) {
        this.messagingTemplate = messagingTemplate;
        this.wordRepository = wordRepository;
        this.matchRepository = matchRepository;
    }

    // Nuevo: inicializa y guarda el estado en activeGames, retorna el estado creado
    public MemoramaGameState initializeGameAndStore(Match match) {
        String roomCode = match.getRoomCode();
        Long themeId = match.getThemes().stream()
                .findFirst()
                .map(t -> t.getId())
                .orElseThrow(() -> new IllegalStateException("Match sin temas"));

        List<String> playerNicknames = new ArrayList<>(match.getPlayers());
        MemoramaGameState gameState = buildInitialState(roomCode, themeId, playerNicknames);

        activeGames.put(roomCode, gameState);

        // publicar estado inicial
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, gameState);
        return gameState;
    }

    // Extraído para claridad
    private MemoramaGameState buildInitialState(String roomCode, Long themeId, List<String> playerNicknames) {
        List<Word> words = wordRepository.findByThemeId(themeId);
        List<Word> selectedWords = words.stream().limit(8).toList();

        List<MemoramaCard> cards = new ArrayList<>();
        int position = 0;
        for (Word word : selectedWords) {
            cards.add(new MemoramaCard(position++, word.getId(), word.getCharacter(), "HANZI", false, false));
            cards.add(new MemoramaCard(position++, word.getId(), word.getPinyin(), "PINYIN", false, false));
        }

        Collections.shuffle(cards);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }

        MemoramaGameState gameState = new MemoramaGameState();
        gameState.setRoomCode(roomCode);
        gameState.setCards(cards);
        gameState.setCurrentPlayerNickname(playerNicknames.get(0));
        gameState.setWaitingForFlip(true);

        for (String nickname : playerNicknames) {
            gameState.getPlayerScores().put(nickname, 0);
        }
        return gameState;
    }

    // Expone el estado guardado
    public MemoramaGameState getState(String roomCode) {
        return activeGames.get(roomCode);
    }

    // Maneja el flip; actualiza el estado en memoria y publica via STOMP
    public GameStateResponse flipCard(FlipCardRequest request) {
        String roomCode = request.getRoomCode();
        Integer position = request.getCardPosition();

        MemoramaGameState state = activeGames.get(roomCode);
        if (state == null) {
            // Opcional: enviar error por topic de usuario
            return null;
        }

        if (position == null || position < 0 || position >= state.getCards().size()) {
            return null;
        }

        MemoramaCard card = state.getCards().get(position);
        if (card.isMatched() || card.isFlipped()) {
            return null;
        }

        card.setFlipped(true);

        // Aquí añade la lógica real de comprobación de pares, puntuación, cambio de turno, etc.
        // Por simplicidad se alterna el flag:
        state.setWaitingForFlip(!state.isWaitingForFlip());

        // publicar estado actualizado
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, state);

        // Construir y devolver un GameStateResponse (usa tu DTO real)
        GameStateResponse response = new GameStateResponse();
        // TODO: mapear fields relevantes desde 'state' al DTO
        return response;
    }
}