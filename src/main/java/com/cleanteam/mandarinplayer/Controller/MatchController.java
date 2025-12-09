package com.cleanteam.mandarinplayer.Controller;

import com.cleanteam.mandarinplayer.DTO.*;
import com.cleanteam.mandarinplayer.Game.GameType;
import com.cleanteam.mandarinplayer.Model.Match;
import com.cleanteam.mandarinplayer.Model.Theme;
import com.cleanteam.mandarinplayer.Repository.GameModeRepository;
import com.cleanteam.mandarinplayer.Repository.MatchRepository;
import com.cleanteam.mandarinplayer.Repository.ThemeRepository;
import com.cleanteam.mandarinplayer.Service.MatchConfigurer;
import com.cleanteam.mandarinplayer.Service.MatchService;
import com.cleanteam.mandarinplayer.Service.MatchStateManager;
import com.cleanteam.mandarinplayer.Service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final ThemeRepository themeRepository;
    private final GameModeRepository gameModeRepository;
    private final MatchRepository matchRepository;
    private final MatchConfigurer matchConfigurer;
    private final MatchStateManager matchStateManager;

    public MatchController(ThemeRepository themeRepository,
            GameModeRepository gameModeRepository,
            MatchRepository matchRepository,
            MatchConfigurer matchConfigurer,
            MatchStateManager matchStateManager) {
        this.themeRepository = themeRepository;
        this.gameModeRepository = gameModeRepository;
        this.matchRepository = matchRepository;
        this.matchConfigurer = matchConfigurer;
        this.matchStateManager = matchStateManager;
    }

    @GetMapping("/themes")
    public Object getThemes() {
        return themeRepository.findAll();
    }

    @GetMapping("/gamemodes")
    public Object getGameModes() {
        return gameModeRepository.findAll();
    }

    @PostMapping("/create")
    public Match createMatch(@RequestBody CreateMatchRequest request) {
        Match match = matchConfigurer.configure(request);
        return matchRepository.save(match);
    }

    @PostMapping("/start")
    public Match start(@RequestBody StartMatchRequest req) {
        return matchStateManager.start(req);
    }
}