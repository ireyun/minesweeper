package com.minesweeper.game.controller;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/game")
public class GameController {

    // ==================== 游戏创建 ====================

    /**
     * 创建新游戏
     * POST /api/game/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GameStateResponse>> createGame(
            @RequestBody @Valid CreateGameRequest request) {
        // 业务逻辑：生成地图，创建游戏会话，返回初始游戏状态
    }


    // ==================== 游戏操作 ====================

    /**
     * 处理玩家操作（点击、标记等）
     * POST /api/game/action
     */
    @PostMapping("/action")
    public ResponseEntity<ApiResponse<GameStateResponse>> handlePlayerAction(
            @RequestBody @Valid PlayerActionRequest request) {
        // 业务逻辑：处理格子点击、标记等操作，更新游戏状态
    }

    /**
     * 批量处理操作（用于实现双击展开等高级操作）
     * POST /api/game/batch-actions
     */
    @PostMapping("/batch-actions")
    public ResponseEntity<ApiResponse<GameStateResponse>> handleBatchActions(
            @RequestBody @Valid BatchActionsRequest request) {
        // 业务逻辑：一次性处理多个操作（如双击展开周围格子）
    }



    // ==================== 游戏控制 ====================

    /**
     * 暂停游戏
     * POST /api/game/{gameId}/pause
     */
    @PostMapping("/{gameId}/pause")
    public ResponseEntity<ApiResponse<GameStateResponse>> pauseGame(
            @PathVariable String gameId) {
        // 业务逻辑：暂停游戏计时
    }

    /**
     * 恢复游戏
     * POST /api/game/{gameId}/resume
     */
    @PostMapping("/{gameId}/resume")
    public ResponseEntity<ApiResponse<GameStateResponse>> resumeGame(
            @PathVariable String gameId) {
        // 业务逻辑：恢复游戏计时
    }

    /**
     * 重新开始游戏（同难度）
     * POST /api/game/{gameId}/restart
     */
    @PostMapping("/{gameId}/restart")
    public ResponseEntity<ApiResponse<GameStateResponse>> restartGame(
            @PathVariable String gameId) {
        // 业务逻辑：使用相同难度重新生成地图，开始新游戏
    }

    /**
     * 放弃游戏
     * POST /api/game/{gameId}/surrender
     */
    @PostMapping("/{gameId}/surrender")
    public ResponseEntity<ApiResponse<GameStateResponse>> surrenderGame(
            @PathVariable String gameId) {
        // 业务逻辑：标记游戏为放弃状态，显示所有地雷
    }

}
