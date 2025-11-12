package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.GameEntity;
import com.minesweeper.game.model.Request.BatchActionRequest;
import com.minesweeper.game.model.Request.CreateGameRequest;
import com.minesweeper.game.model.Request.PlayerActionRequest;
import com.minesweeper.game.model.Response.GameStateResponse;
import com.minesweeper.game.repository.GamePlayerRepository;
import com.minesweeper.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 游戏服务类（使用数据库）
 */
@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    
    @Autowired
    private UserService userService;
    
    private final Random random = new Random();

    /**
     * 创建新游戏
     */
    @Transactional
    public GameStateResponse createGame(CreateGameRequest request) {
        // 验证地雷数量
        int maxMines = request.getWidth() * request.getHeight() - 1;
        if (request.getMineCount() > maxMines) {
            throw new IllegalArgumentException("地雷数量过多，最多只能有 " + maxMines + " 个地雷");
        }

        // 生成游戏ID
        String gameId = generateGameId();

        // 创建游戏实体
        GameEntity gameEntity = new GameEntity();
        gameEntity.setGameId(gameId);
        gameEntity.setWidth(request.getWidth());
        gameEntity.setHeight(request.getHeight());
        gameEntity.setMineCount(request.getMineCount());
        gameEntity.setDifficulty(request.getDifficulty());
        
        // 设置房间ID
        if (request.getRoomId() != null) {
            gameEntity.setRoomId(request.getRoomId());
        }
        
        // 初始化棋盘
        gameEntity.initializeBoard();
        
        // 先保存游戏（确保gameId已设置）
        gameEntity = gameRepository.save(gameEntity);
        
        // 添加玩家（在gameId设置后）
        if (request.getUserId() != null) {
            List<String> players = new ArrayList<>();
            players.add(request.getUserId());
            gameEntity.setPlayerIds(players);
            gameEntity.setCurrentPlayerId(request.getUserId());
        }

        // 保存游戏和玩家关联
        gameEntity = gameRepository.save(gameEntity);
        
        // 确保玩家关联被保存
        if (gameEntity.getGamePlayers() != null && !gameEntity.getGamePlayers().isEmpty()) {
            gamePlayerRepository.saveAll(gameEntity.getGamePlayers());
        }

        // 转换为响应对象
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 在房间中创建游戏（添加房间中的所有玩家）
     */
    @Transactional
    public GameStateResponse createGameInRoom(CreateGameRequest request, List<String> roomPlayerIds) {
        // 验证地雷数量
        int maxMines = request.getWidth() * request.getHeight() - 1;
        if (request.getMineCount() > maxMines) {
            throw new IllegalArgumentException("地雷数量过多，最多只能有 " + maxMines + " 个地雷");
        }

        // 生成游戏ID
        String gameId = generateGameId();

        // 创建游戏实体
        GameEntity gameEntity = new GameEntity();
        gameEntity.setGameId(gameId);
        gameEntity.setWidth(request.getWidth());
        gameEntity.setHeight(request.getHeight());
        gameEntity.setMineCount(request.getMineCount());
        gameEntity.setDifficulty(request.getDifficulty());
        
        // 设置房间ID
        if (request.getRoomId() != null) {
            gameEntity.setRoomId(request.getRoomId());
        }

        // 初始化棋盘
        gameEntity.initializeBoard();

        // 先保存游戏（确保gameId已设置）
        gameEntity = gameRepository.save(gameEntity);

        // 添加房间中的所有玩家（在gameId设置后）
        if (roomPlayerIds != null && !roomPlayerIds.isEmpty()) {
            gameEntity.setPlayerIds(roomPlayerIds);
            // 设置当前玩家
            if (request.getUserId() != null && roomPlayerIds.contains(request.getUserId())) {
                gameEntity.setCurrentPlayerId(request.getUserId());
            } else {
                gameEntity.setCurrentPlayerId(roomPlayerIds.get(0));
            }
        } else if (request.getUserId() != null) {
            List<String> players = new ArrayList<>();
            players.add(request.getUserId());
            gameEntity.setPlayerIds(players);
            gameEntity.setCurrentPlayerId(request.getUserId());
        }

        // 保存游戏和玩家关联
        gameEntity = gameRepository.save(gameEntity);
        
        // 确保玩家关联被保存
        if (gameEntity.getGamePlayers() != null && !gameEntity.getGamePlayers().isEmpty()) {
            gamePlayerRepository.saveAll(gameEntity.getGamePlayers());
        }

        // 转换为响应对象
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 处理玩家操作
     */
    @Transactional
    public GameStateResponse handlePlayerAction(PlayerActionRequest request) {
        GameEntity gameEntity = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        
        if (!gameEntity.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏已结束或已暂停");
        }

        boolean success = false;
        switch (request.getAction().toUpperCase()) {
            case "CLICK":
                success = gameEntity.revealCell(request.getRow(), request.getCol());
                break;
            case "FLAG":
                if (!gameEntity.getFlagged()[request.getRow()][request.getCol()]) {
                    success = gameEntity.toggleFlag(request.getRow(), request.getCol());
                }
                break;
            case "UNFLAG":
                if (gameEntity.getFlagged()[request.getRow()][request.getCol()]) {
                    success = gameEntity.toggleFlag(request.getRow(), request.getCol());
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的操作类型: " + request.getAction());
        }

        if (!success) {
            throw new IllegalStateException("操作失败，请检查坐标和操作类型");
        }

        // 保存游戏状态
        gameEntity = gameRepository.save(gameEntity);
        
        // 如果游戏结束，更新用户统计
        if (gameEntity.getGameStatus().equals("WON") || gameEntity.getGameStatus().equals("LOST")) {
            String currentPlayerId = gameEntity.getCurrentPlayerId();
            if (currentPlayerId != null) {
                boolean won = gameEntity.getGameStatus().equals("WON");
                userService.updateUserGameStats(currentPlayerId, won);
            }
        }

        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 批量处理操作
     */
    @Transactional
    public GameStateResponse handleBatchActions(BatchActionRequest request) {
        GameEntity gameEntity = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        
        if (!gameEntity.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏已结束或已暂停");
        }

        // 依次处理每个操作
        for (PlayerActionRequest actionRequest : request.getActions()) {
            try {
                // 更新gameId以确保操作在同一个游戏上
                actionRequest.setGameId(request.getGameId());
                handlePlayerAction(actionRequest);
                // 重新加载游戏实体
                gameEntity = gameRepository.findById(request.getGameId()).orElse(gameEntity);
            } catch (Exception e) {
                // 如果某个操作失败，继续处理其他操作
            }
        }

        // 重新加载最新状态
        gameEntity = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));

        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 暂停游戏
     */
    @Transactional
    public GameStateResponse pauseGame(String gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        
        if (!gameEntity.getGameStatus().equals("PLAYING")) {
            throw new IllegalStateException("游戏不在进行中");
        }

        gameEntity.setGameStatus("PAUSED");
        if (gameEntity.getLastPauseTime() == null) {
            gameEntity.setLastPauseTime(System.currentTimeMillis());
            if (gameEntity.getStartTime() != null) {
                gameEntity.setPausedTime(System.currentTimeMillis() - gameEntity.getStartTime());
            }
        }

        gameEntity = gameRepository.save(gameEntity);
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 恢复游戏
     */
    @Transactional
    public GameStateResponse resumeGame(String gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        
        if (!gameEntity.getGameStatus().equals("PAUSED")) {
            throw new IllegalStateException("游戏未暂停");
        }

        gameEntity.setGameStatus("PLAYING");
        if (gameEntity.getLastPauseTime() != null) {
            gameEntity.setPausedTime(gameEntity.getPausedTime() + 
                (System.currentTimeMillis() - gameEntity.getLastPauseTime()));
            gameEntity.setLastPauseTime(null);
        }
        gameEntity.setStartTime(System.currentTimeMillis() - gameEntity.getPausedTime());

        gameEntity = gameRepository.save(gameEntity);
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 重启游戏
     */
    @Transactional
    public GameStateResponse restartGame(String gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));

        // 保存游戏配置
        String roomId = gameEntity.getRoomId();
        List<String> players = gameEntity.getPlayerIds();

        // 删除旧的玩家关联
        gamePlayerRepository.deleteByGameId(gameId);

        // 重新初始化棋盘（会重置所有状态）
        gameEntity.initializeBoard();
        gameEntity.setRoomId(roomId);
        gameEntity.setPlayerIds(players);
        if (!players.isEmpty()) {
            gameEntity.setCurrentPlayerId(players.get(0));
        }

        // 保存游戏（会自动保存关联的玩家）
        gameEntity = gameRepository.save(gameEntity);
        
        // 确保玩家关联被保存
        if (gameEntity.getGamePlayers() != null && !gameEntity.getGamePlayers().isEmpty()) {
            gamePlayerRepository.saveAll(gameEntity.getGamePlayers());
        }

        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 放弃游戏
     */
    @Transactional
    public GameStateResponse surrenderGame(String gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));

        gameEntity.setGameStatus("SURRENDERED");
        // 揭示所有地雷
        boolean[][] revealed = gameEntity.getRevealed();
        Integer[][] board = gameEntity.getBoard();
        for (int i = 0; i < gameEntity.getHeight(); i++) {
            for (int j = 0; j < gameEntity.getWidth(); j++) {
                if (board[i][j] == -1) {
                    revealed[i][j] = true;
                }
            }
        }
        gameEntity.setRevealed(revealed);

        gameEntity = gameRepository.save(gameEntity);
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 获取游戏状态
     */
    public GameStateResponse getGameState(String gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        return convertToGameStateResponse(gameEntity);
    }

    /**
     * 将GameEntity转换为GameStateResponse
     */
    private GameStateResponse convertToGameStateResponse(GameEntity gameEntity) {
        GameStateResponse response = new GameStateResponse();
        response.setGameId(gameEntity.getGameId());
        response.setWidth(gameEntity.getWidth());
        response.setHeight(gameEntity.getHeight());
        response.setMineCount(gameEntity.getMineCount());
        response.setGameStatus(gameEntity.getGameStatus());
        response.setElapsedTime(gameEntity.getElapsedTime());
        response.setStartTime(gameEntity.getStartTime());
        response.setPlayers(new ArrayList<>(gameEntity.getPlayerIds()));
        response.setCurrentPlayerId(gameEntity.getCurrentPlayerId());

        // 构建棋盘状态
        // board: -1表示地雷，0-8表示周围地雷数，-2表示未揭示，-3表示标记
        Integer[][] board = new Integer[gameEntity.getHeight()][gameEntity.getWidth()];
        boolean[][] revealed = gameEntity.getRevealed();
        boolean[][] flagged = gameEntity.getFlagged();
        boolean gameEnded = gameEntity.getGameStatus().equals("LOST") || 
                           gameEntity.getGameStatus().equals("WON") || 
                           gameEntity.getGameStatus().equals("SURRENDERED");

        for (int i = 0; i < gameEntity.getHeight(); i++) {
            for (int j = 0; j < gameEntity.getWidth(); j++) {
                if (revealed[i][j] || gameEnded) {
                    board[i][j] = gameEntity.getBoard()[i][j];
                } else if (flagged[i][j]) {
                    board[i][j] = -3;
                } else {
                    board[i][j] = -2;
                }
            }
        }

        response.setBoard(board);
        response.setRevealed(gameEntity.getRevealed());
        response.setFlagged(gameEntity.getFlagged());

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
    @Transactional
    public void deleteGame(String gameId) {
        gamePlayerRepository.deleteByGameId(gameId);
        gameRepository.deleteById(gameId);
    }
}
