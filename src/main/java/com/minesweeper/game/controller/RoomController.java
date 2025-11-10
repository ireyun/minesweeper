package com.minesweeper.game.controller;

import com.minesweeper.game.model.Request.CreateGameRequest;
import com.minesweeper.game.model.Request.CreateRoomRequest;
import com.minesweeper.game.model.Response.APIResponse;
import com.minesweeper.game.model.Response.GameStateResponse;
import com.minesweeper.game.model.Response.JoinRoomResponse;
import com.minesweeper.game.model.Response.RoomResponse;
import com.minesweeper.game.service.GameService;
import com.minesweeper.game.service.RoomService;
import com.minesweeper.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * 房间控制器
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    /**
     * 创建房间
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<APIResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 验证用户身份
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<RoomResponse> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = extractToken(authHeader);
            String userId = userService.getUserIdByToken(token);
            if (userId == null || !userService.validateToken(token)) {
                APIResponse<RoomResponse> response = APIResponse.error(401, "无效的认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 设置房主ID
            request.setHostId(userId);

            RoomResponse room = roomService.createRoom(request);
            APIResponse<RoomResponse> response = APIResponse.success("房间创建成功", room);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            APIResponse<RoomResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 加入房间
     * POST /api/rooms/{roomId}/join
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<APIResponse<JoinRoomResponse>> joinRoom(
            @PathVariable String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 验证用户身份
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<JoinRoomResponse> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = extractToken(authHeader);
            String userId = userService.getUserIdByToken(token);
            if (userId == null || !userService.validateToken(token)) {
                APIResponse<JoinRoomResponse> response = APIResponse.error(401, "无效的认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            JoinRoomResponse joinResponse = roomService.joinRoom(roomId, userId);
            APIResponse<JoinRoomResponse> response = APIResponse.success(joinResponse.getMessage(), joinResponse);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<JoinRoomResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            APIResponse<JoinRoomResponse> response = APIResponse.error(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 离开房间
     * POST /api/rooms/{roomId}/leave
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<APIResponse<Void>> leaveRoom(
            @PathVariable String roomId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 验证用户身份
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<Void> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = extractToken(authHeader);
            String userId = userService.getUserIdByToken(token);
            if (userId == null || !userService.validateToken(token)) {
                APIResponse<Void> response = APIResponse.error(401, "无效的认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            roomService.leaveRoom(roomId, userId);
            APIResponse<Void> response = APIResponse.success("已离开房间", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<Void> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 获取房间信息
     * GET /api/rooms/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<APIResponse<RoomResponse>> getRoom(@PathVariable String roomId) {
        try {
            RoomResponse room = roomService.getRoom(roomId);
            APIResponse<RoomResponse> response = APIResponse.success(room);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<RoomResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 获取所有房间列表
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<RoomResponse>>> getAllRooms() {
        try {
            List<RoomResponse> rooms = roomService.getAllRooms();
            APIResponse<List<RoomResponse>> response = APIResponse.success(rooms);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            APIResponse<List<RoomResponse>> response = APIResponse.error(500, "获取房间列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 在房间中创建游戏
     * POST /api/rooms/{roomId}/create-game
     */
    @PostMapping("/{roomId}/create-game")
    public ResponseEntity<APIResponse<GameStateResponse>> createGameInRoom(
            @PathVariable String roomId,
            @Valid @RequestBody CreateGameRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 验证用户身份
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<GameStateResponse> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = extractToken(authHeader);
            String userId = userService.getUserIdByToken(token);
            if (userId == null || !userService.validateToken(token)) {
                APIResponse<GameStateResponse> response = APIResponse.error(401, "无效的认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 验证房间是否存在
            RoomResponse room = roomService.getRoom(roomId);
            if (room == null) {
                APIResponse<GameStateResponse> response = APIResponse.error(404, "房间不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 验证用户是否是房间成员
            if (!room.getPlayerIds().contains(userId)) {
                APIResponse<GameStateResponse> response = APIResponse.error(403, "您不是房间成员，无法创建游戏");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 检查房间是否已有游戏
            if (room.getCurrentGameId() != null && !room.getCurrentGameId().isEmpty()) {
                APIResponse<GameStateResponse> response = APIResponse.error(409, "房间已有游戏进行中");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // 设置房间ID和用户ID
            request.setRoomId(roomId);
            request.setUserId(userId);

            // 创建游戏（添加房间中的所有玩家）
            GameStateResponse gameState = gameService.createGameInRoom(request, room.getPlayerIds());

            // 更新房间的游戏ID和状态
            roomService.setRoomGame(roomId, gameState.getGameId());

            APIResponse<GameStateResponse> response = APIResponse.success("游戏创建成功", gameState);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            APIResponse<GameStateResponse> response = APIResponse.error(409, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 从Authorization header中提取token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
