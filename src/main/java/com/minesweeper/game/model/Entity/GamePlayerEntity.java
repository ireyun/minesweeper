package com.minesweeper.game.model.Entity;

import javax.persistence.*;

/**
 * 游戏玩家关联实体
 */
@Entity
@Table(name = "game_player")
public class GamePlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "game_id", length = 64, nullable = false)
    private String gameId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "join_time", nullable = false)
    private Long joinTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    private GameEntity game;

    @PrePersist
    protected void onCreate() {
        if (joinTime == null) {
            joinTime = System.currentTimeMillis();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
}

