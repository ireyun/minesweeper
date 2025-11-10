package com.minesweeper.game.controller;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/user")
public class UserController {

    // ==================== 认证相关 ====================

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserInfoResponse>> register(@RequestBody @Valid RegisterRequest request) {
        // 业务逻辑：创建新用户，返回用户基本信息（不包含密码）
    }

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        // 业务逻辑：验证用户凭证，生成JWT令牌，返回登录结果
    }

    /**
     * 用户登出
     * POST /api/user/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        // 业务逻辑：使令牌失效，清理用户会话
    }

}
