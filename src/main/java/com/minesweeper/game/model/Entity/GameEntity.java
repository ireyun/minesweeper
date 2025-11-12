package com.minesweeper.game.model.Entity;

import com.minesweeper.game.util.BooleanArray2DConverter;
import com.minesweeper.game.util.IntegerArray2DConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏实体类（JPA）
 */
@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    @Column(name = "game_id", length = 64)
    private String gameId;

    @Column(name = "room_id", length = 64)
    private String roomId;

    @Column(name = "width", nullable = false)
    private Integer width;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "mine_count", nullable = false)
    private Integer mineCount;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "board", columnDefinition = "TEXT")
    @Convert(converter = IntegerArray2DConverter.class)
    private Integer[][] board;

    @Column(name = "revealed", columnDefinition = "TEXT")
    @Convert(converter = BooleanArray2DConverter.class)
    private boolean[][] revealed;

    @Column(name = "flagged", columnDefinition = "TEXT")
    @Convert(converter = BooleanArray2DConverter.class)
    private boolean[][] flagged;

    @Column(name = "game_status", length = 20, nullable = false)
    private String gameStatus = "PLAYING";

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "paused_time")
    private Long pausedTime = 0L;

    @Column(name = "last_pause_time")
    private Long lastPauseTime;

    @Column(name = "current_player_id", length = 64)
    private String currentPlayerId;

    @Column(name = "revealed_count", nullable = false)
    private Integer revealedCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GamePlayerEntity> gamePlayers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new java.util.Date();
        }
        if (gameStatus == null) {
            gameStatus = "PLAYING";
        }
        if (revealedCount == null) {
            revealedCount = 0;
        }
        if (pausedTime == null) {
            pausedTime = 0L;
        }
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getPausedTime() {
        return pausedTime;
    }

    public void setPausedTime(Long pausedTime) {
        this.pausedTime = pausedTime;
    }

    public Long getLastPauseTime() {
        return lastPauseTime;
    }

    public void setLastPauseTime(Long lastPauseTime) {
        this.lastPauseTime = lastPauseTime;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public Integer getRevealedCount() {
        return revealedCount;
    }

    public void setRevealedCount(Integer revealedCount) {
        this.revealedCount = revealedCount;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<GamePlayerEntity> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<GamePlayerEntity> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    /**
     * 初始化棋盘（创建新游戏时调用）
     */
    public void initializeBoard() {
        if (board == null || revealed == null || flagged == null) {
            board = new Integer[height][width];
            revealed = new boolean[height][width];
            flagged = new boolean[height][width];
        }

        // 初始化所有格子为0
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = 0;
                revealed[i][j] = false;
                flagged[i][j] = false;
            }
        }

        // 随机放置地雷
        int minesPlaced = 0;
        java.util.Random random = new java.util.Random();
        while (minesPlaced < mineCount) {
            int row = random.nextInt(height);
            int col = random.nextInt(width);
            if (board[row][col] != -1) {
                board[row][col] = -1;
                minesPlaced++;
            }
        }

        // 计算每个格子周围的地雷数
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] != -1) {
                    board[i][j] = countAdjacentMines(i, j);
                }
            }
        }

        revealedCount = 0;
        startTime = System.currentTimeMillis();
        pausedTime = 0L;
        gameStatus = "PLAYING";
    }

    /**
     * 计算周围地雷数
     */
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                    if (board[newRow][newCol] == -1) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 揭示格子
     */
    public boolean revealCell(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        }
        if (revealed[row][col] || flagged[row][col]) {
            return false;
        }

        revealed[row][col] = true;
        revealedCount++;

        // 如果是地雷，游戏结束
        if (board[row][col] == -1) {
            gameStatus = "LOST";
            return true;
        }

        // 如果揭示的格子是0，自动揭示周围的格子
        if (board[row][col] == 0) {
            revealAdjacentCells(row, col);
        }

        // 检查是否获胜
        if (checkWin()) {
            gameStatus = "WON";
        }

        return true;
    }

    /**
     * 递归揭示周围的空格子
     */
    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                    if (!revealed[newRow][newCol] && !flagged[newRow][newCol]) {
                        revealed[newRow][newCol] = true;
                        revealedCount++;
                        if (board[newRow][newCol] == 0) {
                            revealAdjacentCells(newRow, newCol);
                        }
                    }
                }
            }
        }
    }

    /**
     * 标记/取消标记格子
     */
    public boolean toggleFlag(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        }
        if (revealed[row][col]) {
            return false;
        }
        flagged[row][col] = !flagged[row][col];
        return true;
    }

    /**
     * 检查是否获胜
     */
    public boolean checkWin() {
        int totalCells = width * height;
        return revealedCount == (totalCells - mineCount);
    }

    /**
     * 获取已用时间（毫秒）
     */
    public Long getElapsedTime() {
        if (gameStatus.equals("PAUSED") && lastPauseTime != null) {
            return pausedTime + (lastPauseTime - startTime);
        }
        if (startTime != null) {
            if (pausedTime > 0) {
                return pausedTime + (System.currentTimeMillis() - startTime);
            }
            return System.currentTimeMillis() - startTime;
        }
        return 0L;
    }

    /**
     * 获取玩家ID列表
     */
    public List<String> getPlayerIds() {
        List<String> playerIds = new ArrayList<>();
        if (gamePlayers != null) {
            for (GamePlayerEntity gamePlayer : gamePlayers) {
                playerIds.add(gamePlayer.getPlayerId());
            }
        }
        return playerIds;
    }

    /**
     * 设置玩家ID列表
     */
    public void setPlayerIds(List<String> playerIds) {
        if (gamePlayers == null) {
            gamePlayers = new ArrayList<>();
        }
        gamePlayers.clear();
        if (playerIds != null) {
            for (String playerId : playerIds) {
                GamePlayerEntity gamePlayer = new GamePlayerEntity();
                gamePlayer.setGameId(this.gameId);
                gamePlayer.setPlayerId(playerId);
                gamePlayers.add(gamePlayer);
            }
        }
    }
}

