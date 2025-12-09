// java
package com.cleanteam.mandarinplayer.Model;

import com.cleanteam.mandarinplayer.Game.Game;
import com.cleanteam.mandarinplayer.Game.GameType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", unique = true, length = 16, nullable = false)
    private String roomCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "match_themes",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> themes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "match_players", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "player_name")
    private Set<String> players = new HashSet<>();

    // Este campo no se persiste en la BD; mantiene la instancia en memoria
    @JsonIgnore
    private transient Game game;

    // Nuevo: JSON serializado del estado del juego (respaldo/persistencia)
    @Lob
    @Column(name = "game_state_json", columnDefinition = "TEXT")
    private String gameStateJson;

    public Long getId() { return id; }

    public String getRoomCode() { return roomCode; }

    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public MatchStatus getStatus() { return status; }

    public void setStatus(MatchStatus status) { this.status = status; }

    public GameType getGameType() { return gameType; }

    public void setGameType(GameType gameType) { this.gameType = gameType; }

    public Set<Theme> getThemes() { return themes; }

    public void setThemes(Set<Theme> themes) { this.themes = themes; }

    public Set<String> getPlayers() { return players; }

    public void setPlayers(Set<String> players) { this.players = players; }

    public Game getGame() { return game; }

    public void setGame(Game game) { this.game = game; }

    public String getGameStateJson() { return gameStateJson; }

    public void setGameStateJson(String gameStateJson) { this.gameStateJson = gameStateJson; }
}