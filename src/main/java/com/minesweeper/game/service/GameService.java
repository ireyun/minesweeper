package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.Game;
import com.minesweeper.game.model.Request.BatchActionRequest;
import com.minesweeper.game.model.Request.CreateGameRequest;
import com.minesweeper.game.model.Request.PlayerActionRequest;
import com.minesweeper.game.model.Response.GameStateResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏服务类
 */
@Service
public class GameService {
    // 使用内存存储游戏数据（后续可以替换为Repository）
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * 创建新游戏
     */
    public GameStateResponse createGame(CreateGameRequest request) {
        // 验证地雷数量
        int maxMines = request.getWidth() * request.getHeight() - 1;
        if (request.getMineCount() > maxMines) {
            throw new IllegalArgumentException("地雷数量过多，最多只能有 " + maxMines + " 个地雷");
        }

        // 生成游戏ID
        String gameId = generateGameId();

        // 创建游戏实体
        Game game = new Game(gameId, request.getWidth(), request.getHeight(), 
                            request.getMineCount(), request.getDifficulty());
        
        // 设置房间ID和玩家
        if (request.getRoomId() != null) {
            game.setRoomId(request.getRoomId());
        }
        if (request.getUserId() != null) {
            game.getPlayers().add(request.getUserId());
            game.setCurrentPlayerId(request.getUserId());
        }

        // 保存游戏
        games.put(gameId, game);

        // 转换为响应对象
        return convertToGameStateResponse(game);
    }

    /**
     * 在房间中创建游戏（添加房间中的所有玩家）
     */
    public GameStateResponse createGameInRoom(CreateGameRequest request, List<String> roomPlayerIds) {
        // 验证地雷数量
        int maxMines = request.getWidth() * request.getHeight() - 1;
        if (request.getMineCount() > maxMines) {
            throw new IllegalArgumentException("地雷数量过多，最多只能有 " + maxMines + " 个地雷");
        }

        // 生成游戏ID
        String gameId = generateGameId();

        // 创建游戏实体
        Game game = new Game(gameId, request.getWidth(), request.getHeight(), 
                            request.getMineCount(), request.getDifficulty());
        
        // 设置房间ID
        if (request.getRoomId() != null) {
            game.setRoomId(request.getRoomId());
        }

        // 添加房间中的所有玩家
        if (roomPlayerIds != null && !roomPlayerIds.isEmpty()) {
            game.getPlayers().addAll(roomPlayerIds);
            // 设置当前玩家为第一个玩家（或创建者）
            if (request.getUserId() != null && roomPlayerIds.contains(request.getUserId())) {
                game.setCurrentPlayerId(request.getUserId());
            } else {
                game.setCurrentPlayerId(roomPlayerIds.get(0));
            }
        } else if (request.getUserId() != null) {
            game.getPlayers().add(request.getUserId());
            game.setCurrentPlayerId(request.getUserId());
        }

        // 保存游戏
        games.put(gameId, game);

        // 转换为响应对象
        return convertToGameStateResponse(game);
    }

    /**
     * 处理玩家操作
     */
    public GameStateResponse handlePlayerAction(PlayerActionRequest request) {
        Game game = games.get(request.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        if (!game.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏已结束或已暂停");
        }

        boolean success = false;
        switch (request.getAction().toUpperCase()) {
            case "CLICK":
                success = game.revealCell(request.getRow(), request.getCol());
                break;
            case "FLAG":
                if (!game.getFlagged()[request.getRow()][request.getCol()]) {
                    success = game.toggleFlag(request.getRow(), request.getCol());
                }
                break;
            case "UNFLAG":
                if (game.getFlagged()[request.getRow()][request.getCol()]) {
                    success = game.toggleFlag(request.getRow(), request.getCol());
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的操作类型: " + request.getAction());
        }

        if (!success) {
            throw new IllegalStateException("操作失败，请检查坐标和操作类型");
        }

        return convertToGameStateResponse(game);
    }

    /**
     * 批量处理操作
     */
    public GameStateResponse handleBatchActions(BatchActionRequest request) {
        Game game = games.get(request.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        if (!game.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏已结束或已暂停");
        }

        // 依次处理每个操作
        for (PlayerActionRequest actionRequest : request.getActions()) {
            try {
                handlePlayerAction(actionRequest);
            } catch (Exception e) {
                // 如果某个操作失败，继续处理其他操作
                // 实际应用中可能需要更详细的错误处理
            }
        }

        return convertToGameStateResponse(game);
    }

    /**
     * 暂停游戏
     */
    public GameStateResponse pauseGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        if (!game.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏不在进行中");
        }

        game.setGameStatus("PAUSED");
        if (game.getLastPauseTime() == null) {
            game.setLastPauseTime(System.currentTimeMillis());
            if (game.getStartTime() != null) {
                game.setPausedTime(System.currentTimeMillis() - game.getStartTime());
            }
        }

        return convertToGameStateResponse(game);
    }

    /**
     * 恢复游戏
     */
    public GameStateResponse resumeGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        if (!game.getGameStatus().equals("PAUSED")) {
            throw new IllegalStateException("游戏未暂停");
        }

        game.setGameStatus("PLAYING");
        if (game.getLastPauseTime() != null) {
            game.setPausedTime(game.getPausedTime() + (System.currentTimeMillis() - game.getLastPauseTime()));
            game.setLastPauseTime(null);
        }
        game.setStartTime(System.currentTimeMillis() - game.getPausedTime());

        return convertToGameStateResponse(game);
    }

    /**
     * 重启游戏
     */
    public GameStateResponse restartGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }

        // 保存游戏配置
        Integer width = game.getWidth();
        Integer height = game.getHeight();
        Integer mineCount = game.getMineCount();
        String difficulty = game.getDifficulty();
        String roomId = game.getRoomId();
        List<String> players = new ArrayList<>(game.getPlayers());

        // 创建新游戏
        Game newGame = new Game(gameId, width, height, mineCount, difficulty);
        newGame.setRoomId(roomId);
        newGame.setPlayers(players);
        if (!players.isEmpty()) {
            newGame.setCurrentPlayerId(players.get(0));
        }

        // 替换旧游戏
        games.put(gameId, newGame);

        return convertToGameStateResponse(newGame);
    }

    /**
     * 放弃游戏
     */
    public GameStateResponse surrenderGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }

        game.setGameStatus("SURRENDERED");
        // 揭示所有地雷
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (game.getBoard()[i][j] == -1) {
                    game.getRevealed()[i][j] = true;
                }
            }
        }

        return convertToGameStateResponse(game);
    }

    /**
     * 获取游戏状态
     */
    public GameStateResponse getGameState(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        return convertToGameStateResponse(game);
    }

    /**
     * 将Game实体转换为GameStateResponse
     */
    private GameStateResponse convertToGameStateResponse(Game game) {
        GameStateResponse response = new GameStateResponse();
        response.setGameId(game.getGameId());
        response.setWidth(game.getWidth());
        response.setHeight(game.getHeight());
        response.setMineCount(game.getMineCount());
        response.setGameStatus(game.getGameStatus());
        response.setElapsedTime(game.getElapsedTime());
        response.setStartTime(game.getStartTime());
        response.setPlayers(new ArrayList<>(game.getPlayers()));
        response.setCurrentPlayerId(game.getCurrentPlayerId());

        // 构建棋盘状态
        // board: -1表示地雷，0-8表示周围地雷数，-2表示未揭示，-3表示标记
        // 如果游戏已结束（LOST或SURRENDERED），显示所有地雷
        Integer[][] board = new Integer[game.getHeight()][game.getWidth()];
        boolean[][] revealed = game.getRevealed();
        boolean[][] flagged = game.getFlagged();
        boolean gameEnded = game.getGameStatus().equals("LOST") || 
                           game.getGameStatus().equals("WON") || 
                           game.getGameStatus().equals("SURRENDERED");

        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (revealed[i][j] || gameEnded) {
                    // 已揭示的格子或游戏结束时，显示实际值
                    board[i][j] = game.getBoard()[i][j];
                } else if (flagged[i][j]) {
                    // 标记的格子，显示-3
                    board[i][j] = -3;
                } else {
                    // 未揭示的格子，显示-2
                    board[i][j] = -2;
                }
            }
        }

        response.setBoard(board);
        response.setRevealed(game.getRevealed());
        response.setFlagged(game.getFlagged());

        return response;
    }

    /**
     * 生成游戏ID
     */
    private String generateGameId() {
        return "GAME_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    /**
     * 删除游戏
     */
    public void deleteGame(String gameId) {
        games.remove(gameId);
    }
}
