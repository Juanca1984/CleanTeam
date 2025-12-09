
package com.cleanteam.mandarinplayer.Game;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Service.MemoramaGameService;
import org.springframework.stereotype.Component;

@Component
public class MemoramaGameFactory implements GameFactory {

    private final MemoramaGameService memoramaGameService;

    public MemoramaGameFactory(MemoramaGameService memoramaGameService) {
        this.memoramaGameService = memoramaGameService;
    }

    @Override
    public Game createGame(Match match) {
        return new MemoramaGame(match, memoramaGameService);
    }

    public class MemoramaGame implements Game {

        private final Match match;
        private final MemoramaGameService service;

        public MemoramaGame(Match match, MemoramaGameService service) {
            this.match = match;
            this.service = service;
        }

        @Override
        public void start() {
            service.initializeGameAndStore(match);
        }

        @Override
        public void onFlip(FlipCardRequest req) {
            // Delegar la lógica de flip al servicio:
            // - validar turno/jugador
            // - revelar carta
            // - comprobar pareja
            // - actualizar estado en BD si aplica
            // - emitir eventos (WebSocket) a jugadores
            service.flipCard(req);
        }
    }
}