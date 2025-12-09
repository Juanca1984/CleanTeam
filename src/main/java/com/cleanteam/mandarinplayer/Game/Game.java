package com.cleanteam.mandarinplayer.Game;

import com.cleanteam.mandarinplayer.DTO.FlipCardRequest;

public interface Game {
    void start(); // inicializa rondas, carga palabras del/los temas, etc.
    void onFlip(FlipCardRequest req);
}

