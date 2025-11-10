package com.minesweeper.game.model.Response;

import java.util.List;

/**
 * 游戏状态响应
 */
public class GameStateResponse {
    private String gameId;
    private Integer width;
    private Integer height;
    private Integer mineCount;
    private Integer[][] board; // 游戏棋盘，-1表示地雷，0-8表示周围地雷数，-2表示未揭示，-3表示标记
    private boolean[][] revealed; // 已揭示的格子
    private boolean[][] flagged; // 标记的格子
    private String gameStatus; // PLAYING, WON, LOST, PAUSED, SURRENDERED
    private Long elapsedTime; // 已用时间（毫秒）
    private Long startTime; // 开始时间
    private List<String> players; // 玩家列表
    private String currentPlayerId; // 当前玩家ID

    public GameStateResponse() {
    }

    public GameStateResponse(String gameId, Integer width, Integer height, Integer mineCount, 
                            String gameStatus, Long elapsedTime) {
        this.gameId = gameId;
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        this.gameStatus = gameStatus;
        this.elapsedTime = elapsedTime;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getMineCount() {
        return mineCount;
    }

    public void setMineCount(Integer mineCount) {
        this.mineCount = mineCount;
    }

    public Integer[][] getBoard() {
        return board;
    }

    public void setBoard(Integer[][] board) {
        this.board = board;
    }

    public boolean[][] getRevealed() {
        return revealed;
    }

    public void setRevealed(boolean[][] revealed) {
        this.revealed = revealed;
    }

    public boolean[][] getFlagged() {
        return flagged;
    }

    public void setFlagged(boolean[][] flagged) {
        this.flagged = flagged;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }
}
