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

/*
 * MatchGameManager: orquesta la creación/recuperación de juegos por match/roomCode.
 * Delegará a servicios de juego concretos (aquí solo Memorama).
 */
@Service
public class MatchGameManager {

    private final MemoramaGameService memoramaGameService;
    private final MatchRepository matchRepository;

    public MatchGameManager(MemoramaGameService memoramaGameService,
                            MatchRepository matchRepository) {
        this.memoramaGameService = memoramaGameService;
        this.matchRepository = matchRepository;
    }

    public MemoramaGameState createOrGetGameForMatch(Match match) {
        String roomCode = match.getRoomCode();
        MemoramaGameState existing = memoramaGameService.getState(roomCode);
        if (existing != null) {
            return existing;
        }

        return memoramaGameService.initializeGameAndStore(match);
    }

    public GameStateResponse handleFlip(FlipCardRequest request) {
        String roomCode = request.getRoomCode();
        return memoramaGameService.flipCard(request);
    }

    // Método para persistir snapshot (opcional, llamado periódicamente o en eventos)
    public void persistSnapshot(String roomCode) {
        MemoramaGameState state = memoramaGameService.getState(roomCode);
        if (state == null) return;
    }
}


