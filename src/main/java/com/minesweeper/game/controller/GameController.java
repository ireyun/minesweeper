package com.minesweeper.game.controller;

import com.minesweeper.game.model.Request.BatchActionRequest;
import com.minesweeper.game.model.Request.CreateGameRequest;
import com.minesweeper.game.model.Request.PlayerActionRequest;
import com.minesweeper.game.model.Response.APIResponse;
import com.minesweeper.game.model.Response.GameStateResponse;
import com.minesweeper.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 游戏控制器
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    // ==================== 游戏创建 ====================

    /**
     * 创建新游戏
     * POST /api/game/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<GameStateResponse>> createGame(
            @RequestBody @Valid CreateGameRequest request) {
        try {
            GameStateResponse gameState = gameService.createGame(request);
            APIResponse<GameStateResponse> response = APIResponse.success("游戏创建成功", gameState);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 获取游戏状态
     * GET /api/game/{gameId}
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<APIResponse<GameStateResponse>> getGameState(
            @PathVariable String gameId) {
        try {
            GameStateResponse gameState = gameService.getGameState(gameId);
            APIResponse<GameStateResponse> response = APIResponse.success(gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ==================== 游戏操作 ====================

    /**
     * 处理玩家操作（点击、标记等）
     * POST /api/game/action
     */
    @PostMapping("/action")
    public ResponseEntity<APIResponse<GameStateResponse>> handlePlayerAction(
            @RequestBody @Valid PlayerActionRequest request) {
        try {
            GameStateResponse gameState = gameService.handlePlayerAction(request);
            APIResponse<GameStateResponse> response = APIResponse.success("操作成功", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(400, e.getMessage());//参数异常捕获
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(409, e.getMessage());//状态异常捕获
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 批量处理操作（用于实现双击展开等高级操作）
     * POST /api/game/batch-actions
     */
    @PostMapping("/batch-actions")
    public ResponseEntity<APIResponse<GameStateResponse>> handleBatchActions(
            @RequestBody @Valid BatchActionRequest request) {
        try {
            GameStateResponse gameState = gameService.handleBatchActions(request);
            APIResponse<GameStateResponse> response = APIResponse.success("批量操作成功", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    // ==================== 游戏控制 ====================

    /**
     * 暂停游戏
     * POST /api/game/{gameId}/pause
     */
    @PostMapping("/{gameId}/pause")
    public ResponseEntity<APIResponse<GameStateResponse>> pauseGame(
            @PathVariable String gameId) {
        try {
            GameStateResponse gameState = gameService.pauseGame(gameId);
            APIResponse<GameStateResponse> response = APIResponse.success("游戏已暂停", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 恢复游戏
     * POST /api/game/{gameId}/resume
     */
    @PostMapping("/{gameId}/resume")
    public ResponseEntity<APIResponse<GameStateResponse>> resumeGame(
            @PathVariable String gameId) {
        try {
            GameStateResponse gameState = gameService.resumeGame(gameId);
            APIResponse<GameStateResponse> response = APIResponse.success("游戏已恢复", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 重新开始游戏（同难度）
     * POST /api/game/{gameId}/restart
     */
    @PostMapping("/{gameId}/restart")
    public ResponseEntity<APIResponse<GameStateResponse>> restartGame(
            @PathVariable String gameId) {
        try {
            GameStateResponse gameState = gameService.restartGame(gameId);
            APIResponse<GameStateResponse> response = APIResponse.success("游戏已重启", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 放弃游戏
     * POST /api/game/{gameId}/surrender
     */
    @PostMapping("/{gameId}/surrender")
    public ResponseEntity<APIResponse<GameStateResponse>> surrenderGame(
            @PathVariable String gameId) {
        try {
            GameStateResponse gameState = gameService.surrenderGame(gameId);
            APIResponse<GameStateResponse> response = APIResponse.success("游戏已放弃", gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
