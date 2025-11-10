package com.minesweeper.game.controller;

import com.minesweeper.game.model.Request.LoginRequest;
import com.minesweeper.game.model.Request.RegisterRequest;
import com.minesweeper.game.model.Response.APIResponse;
import com.minesweeper.game.model.Response.LoginResponse;
import com.minesweeper.game.model.Response.UserInfoResponse;
import com.minesweeper.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ==================== 认证相关 ====================

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserInfoResponse>> register(@RequestBody @Valid RegisterRequest request) {
        try {
            UserInfoResponse userInfo = userService.register(request);
            APIResponse<UserInfoResponse> response = APIResponse.success("注册成功", userInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            APIResponse<UserInfoResponse> response = APIResponse.error(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        try {
            LoginResponse loginResponse = userService.login(request);
            APIResponse<LoginResponse> response = APIResponse.success("登录成功", loginResponse);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<LoginResponse> response = APIResponse.error(401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 用户登出
     * POST /api/user/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<Void> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 提取token（支持 "Bearer token" 格式）
            String token = extractToken(authHeader);
            userService.logout(token);
            APIResponse<Void> response = APIResponse.success("登出成功", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            APIResponse<Void> response = APIResponse.error(500, "登出失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取用户信息
     * GET /api/user/info
     */
    @GetMapping("/info")
    public ResponseEntity<APIResponse<UserInfoResponse>> getUserInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || authHeader.isEmpty()) {
                APIResponse<UserInfoResponse> response = APIResponse.error(401, "未提供认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = extractToken(authHeader);
            String userId = userService.getUserIdByToken(token);
            if (userId == null) {
                APIResponse<UserInfoResponse> response = APIResponse.error(401, "无效的认证令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            UserInfoResponse userInfo = userService.getUserInfo(userId);
            APIResponse<UserInfoResponse> response = APIResponse.success(userInfo);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            APIResponse<UserInfoResponse> response = APIResponse.error(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
