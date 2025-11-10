package com.minesweeper.game.model.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏实体类
 */
public class Game {
    private String gameId;
    private String roomId;
    private Integer width;
    private Integer height;
    private Integer mineCount;
    private String difficulty;
    private Integer[][] board; // 游戏棋盘，-1表示地雷，0-8表示周围地雷数
    private boolean[][] revealed; // 已揭示的格子
    private boolean[][] flagged; // 标记的格子
    private String gameStatus; // PLAYING, WON, LOST, PAUSED, SURRENDERED
    private Long startTime; // 开始时间
    private Long pausedTime; // 暂停时的累计时间
    private Long lastPauseTime; // 最后暂停的时间点
    private List<String> players; // 玩家列表
    private String currentPlayerId; // 当前玩家ID
    private Integer revealedCount; // 已揭示的格子数量

    public Game() {
        this.players = new ArrayList<>();
        this.gameStatus = "PLAYING";
        this.revealedCount = 0;
    }

    public Game(String gameId, Integer width, Integer height, Integer mineCount, String difficulty) {
        this();
        this.gameId = gameId;
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        this.difficulty = difficulty;
        this.startTime = System.currentTimeMillis();
        this.pausedTime = 0L;
        this.board = new Integer[height][width];
        this.revealed = new boolean[height][width];
        this.flagged = new boolean[height][width];
        initializeBoard();
    }

    /**
     * 初始化棋盘
     */
    private void initializeBoard() {
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

    public Integer getRevealedCount() {
        return revealedCount;
    }

    public void setRevealedCount(Integer revealedCount) {
        this.revealedCount = revealedCount;
    }
}

