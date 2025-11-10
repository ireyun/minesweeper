package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.User;
import com.minesweeper.game.model.Request.LoginRequest;
import com.minesweeper.game.model.Request.RegisterRequest;
import com.minesweeper.game.model.Response.LoginResponse;
import com.minesweeper.game.model.Response.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务类
 */
@Service
public class UserService {
    // 使用内存存储用户数据（后续可以替换为Repository）
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToUserId = new ConcurrentHashMap<>();
    // 简单的Token存储（实际应用中应该使用JWT或其他安全机制）
    private final Map<String, String> tokens = new ConcurrentHashMap<>(); // token -> userId
    private final Map<String, String> userTokens = new ConcurrentHashMap<>(); // userId -> token
    private final Random random = new Random();

    // Token过期时间（毫秒）- 24小时
    private static final long TOKEN_EXPIRY = 24 * 60 * 60 * 1000L;

    /**
     * 用户注册
     */
    public UserInfoResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (usernameToUserId.containsKey(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        for (User user : users.values()) {
            if (user.getEmail() != null && user.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("邮箱已被注册");
            }
        }

        // 生成用户ID
        String userId = generateUserId();

        // 创建用户实体（实际应用中应该对密码进行加密）
        User user = new User(userId, request.getUsername(), request.getPassword(), request.getEmail());

        // 保存用户
        users.put(userId, user);
        usernameToUserId.put(request.getUsername(), userId);

        // 转换为响应对象
        return convertToUserInfoResponse(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        String userId = usernameToUserId.get(request.getUsername());
        if (userId == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 验证密码（实际应用中应该使用加密后的密码比较）
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 生成Token（实际应用中应该使用JWT）
        String token = generateToken(userId);

        // 保存Token
        tokens.put(token, userId);
        // 如果用户已有Token，移除旧的
        String oldToken = userTokens.get(userId);
        if (oldToken != null) {
            tokens.remove(oldToken);
        }
        userTokens.put(userId, token);

        // 创建登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setExpiresIn(TOKEN_EXPIRY / 1000); // 转换为秒

        return response;
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        String userId = tokens.remove(token);
        if (userId != null) {
            userTokens.remove(userId);
        }
    }

    /**
     * 根据Token获取用户ID
     */
    public String getUserIdByToken(String token) {
        return tokens.get(token);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }

    /**
     * 根据用户ID获取用户
     */
    public User getUserById(String userId) {
        return users.get(userId);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        String userId = usernameToUserId.get(username);
        if (userId == null) {
            return null;
        }
        return users.get(userId);
    }

    /**
     * 更新用户游戏统计
     */
    public void updateUserGameStats(String userId, boolean won) {
        User user = users.get(userId);
        if (user != null) {
            user.addGameResult(won);
        }
    }

    /**
     * 将User实体转换为UserInfoResponse
     */
    private UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setTotalGames(user.getTotalGames());
        response.setWins(user.getWins());
        response.setLosses(user.getLosses());
        response.setWinRate(user.getWinRate());
        return response;
    }

    /**
     * 生成用户ID
     */
    private String generateUserId() {
        return "USER_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    /**
     * 生成Token（简单实现，实际应用中应该使用JWT）
     */
    private String generateToken(String userId) {
        return "TOKEN_" + System.currentTimeMillis() + "_" + random.nextInt(100000) + "_" + userId.hashCode();
    }

    /**
     * 获取用户信息
     */
    public UserInfoResponse getUserInfo(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToUserInfoResponse(user);
    }
}
