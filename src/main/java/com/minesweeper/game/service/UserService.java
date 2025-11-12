package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.UserEntity;
import com.minesweeper.game.model.Entity.UserTokenEntity;
import com.minesweeper.game.model.Request.LoginRequest;
import com.minesweeper.game.model.Request.RegisterRequest;
import com.minesweeper.game.model.Response.LoginResponse;
import com.minesweeper.game.model.Response.UserInfoResponse;
import com.minesweeper.game.repository.UserRepository;
import com.minesweeper.game.repository.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

/**
 * 用户服务类（使用数据库）
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserTokenRepository userTokenRepository;
    
    private final Random random = new Random();
    
    // Token过期时间（毫秒）- 24小时
    private static final long TOKEN_EXPIRY = 24 * 60 * 60 * 1000L;

    /**
     * 用户注册
     */
    @Transactional
    public UserInfoResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty() 
            && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 生成用户ID
        String userId = generateUserId();

        // 创建用户实体
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(request.getPassword()); // 实际应用中应该加密
        userEntity.setEmail(request.getEmail());

        // 保存用户
        userEntity = userRepository.save(userEntity);

        // 转换为响应对象
        return convertToUserInfoResponse(userEntity);
    }

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        UserEntity userEntity = userOpt.get();

        // 验证密码（实际应用中应该使用加密后的密码比较）
        if (!userEntity.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 生成Token
        String token = generateToken(userEntity.getUserId());

        // 删除旧的Token
        userTokenRepository.deleteByUserId(userEntity.getUserId());

        // 保存新Token
        UserTokenEntity tokenEntity = new UserTokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUserId(userEntity.getUserId());
        tokenEntity.setExpiresAt(System.currentTimeMillis() + TOKEN_EXPIRY);
        userTokenRepository.save(tokenEntity);

        // 创建登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(userEntity.getUserId());
        response.setUsername(userEntity.getUsername());
        response.setExpiresIn(TOKEN_EXPIRY / 1000); // 转换为秒

        return response;
    }

    /**
     * 用户登出
     */
    @Transactional
    public void logout(String token) {
        userTokenRepository.deleteById(token);
    }

    /**
     * 根据Token获取用户ID
     */
    public String getUserIdByToken(String token) {
        Optional<UserTokenEntity> tokenOpt = userTokenRepository.findById(token);
        if (!tokenOpt.isPresent()) {
            return null;
        }
        
        UserTokenEntity tokenEntity = tokenOpt.get();
        // 检查Token是否过期
        if (tokenEntity.getExpiresAt() < System.currentTimeMillis()) {
            userTokenRepository.delete(tokenEntity);
            return null;
        }
        
        return tokenEntity.getUserId();
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        Optional<UserTokenEntity> tokenOpt = userTokenRepository.findById(token);
        if (!tokenOpt.isPresent()) {
            return false;
        }
        
        UserTokenEntity tokenEntity = tokenOpt.get();
        // 检查Token是否过期
        if (tokenEntity.getExpiresAt() < System.currentTimeMillis()) {
            userTokenRepository.delete(tokenEntity);
            return false;
        }
        
        return true;
    }

    /**
     * 根据用户ID获取用户
     */
    public UserEntity getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * 根据用户名获取用户
     */
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 更新用户游戏统计
     */
    @Transactional
    public void updateUserGameStats(String userId, boolean won) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.addGameResult(won);
            userRepository.save(user);
        }
    }

    /**
     * 将UserEntity转换为UserInfoResponse
     */
    private UserInfoResponse convertToUserInfoResponse(UserEntity userEntity) {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(userEntity.getUserId());
        response.setUsername(userEntity.getUsername());
        response.setEmail(userEntity.getEmail());
        response.setCreatedAt(userEntity.getCreatedAt());
        response.setTotalGames(userEntity.getTotalGames());
        response.setWins(userEntity.getWins());
        response.setLosses(userEntity.getLosses());
        response.setWinRate(userEntity.getWinRate());
        return response;
    }

    /**
     * 生成用户ID
     */
    private String generateUserId() {
        return "USER_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    /**
     * 生成Token
     */
    private String generateToken(String userId) {
        return "TOKEN_" + System.currentTimeMillis() + "_" + random.nextInt(100000) + "_" + userId.hashCode();
    }

    /**
     * 获取用户信息
     */
    public UserInfoResponse getUserInfo(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return convertToUserInfoResponse(userEntity);
    }
}
