-- 扫雷游戏数据库表结构

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS minesweeper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE minesweeper;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `user_id` VARCHAR(64) PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `total_games` INT NOT NULL DEFAULT 0 COMMENT '总游戏数',
    `wins` INT NOT NULL DEFAULT 0 COMMENT '胜利次数',
    `losses` INT NOT NULL DEFAULT 0 COMMENT '失败次数',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 房间表
CREATE TABLE IF NOT EXISTS `room` (
    `room_id` VARCHAR(64) PRIMARY KEY COMMENT '房间ID',
    `room_name` VARCHAR(100) NOT NULL COMMENT '房间名称',
    `host_id` VARCHAR(64) NOT NULL COMMENT '房主ID',
    `host_username` VARCHAR(50) NOT NULL COMMENT '房主用户名',
    `max_players` INT NOT NULL DEFAULT 4 COMMENT '最大玩家数',
    `current_game_id` VARCHAR(64) COMMENT '当前游戏ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '房间状态：WAITING, PLAYING',
    `created_at` BIGINT NOT NULL COMMENT '创建时间（时间戳）',
    INDEX `idx_host_id` (`host_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_current_game_id` (`current_game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- 房间玩家关联表
CREATE TABLE IF NOT EXISTS `room_player` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `room_id` VARCHAR(64) NOT NULL COMMENT '房间ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `player_username` VARCHAR(50) NOT NULL COMMENT '玩家用户名',
    `join_time` BIGINT NOT NULL COMMENT '加入时间（时间戳）',
    UNIQUE KEY `uk_room_player` (`room_id`, `player_id`),
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间玩家关联表';

-- 游戏表
CREATE TABLE IF NOT EXISTS `game` (
    `game_id` VARCHAR(64) PRIMARY KEY COMMENT '游戏ID',
    `room_id` VARCHAR(64) COMMENT '房间ID',
    `width` INT NOT NULL COMMENT '游戏宽度',
    `height` INT NOT NULL COMMENT '游戏高度',
    `mine_count` INT NOT NULL COMMENT '地雷数量',
    `difficulty` VARCHAR(20) COMMENT '难度：EASY, MEDIUM, HARD, CUSTOM',
    `board` TEXT COMMENT '游戏棋盘（JSON格式）',
    `revealed` TEXT COMMENT '已揭示的格子（JSON格式）',
    `flagged` TEXT COMMENT '标记的格子（JSON格式）',
    `game_status` VARCHAR(20) NOT NULL DEFAULT 'PLAYING' COMMENT '游戏状态：PLAYING, WON, LOST, PAUSED, SURRENDERED',
    `start_time` BIGINT COMMENT '开始时间（时间戳）',
    `paused_time` BIGINT DEFAULT 0 COMMENT '暂停时的累计时间（毫秒）',
    `last_pause_time` BIGINT COMMENT '最后暂停的时间点（时间戳）',
    `current_player_id` VARCHAR(64) COMMENT '当前玩家ID',
    `revealed_count` INT NOT NULL DEFAULT 0 COMMENT '已揭示的格子数量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_game_status` (`game_status`),
    INDEX `idx_current_player_id` (`current_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏表';

-- 游戏玩家关联表
CREATE TABLE IF NOT EXISTS `game_player` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `game_id` VARCHAR(64) NOT NULL COMMENT '游戏ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `join_time` BIGINT NOT NULL COMMENT '加入时间（时间戳）',
    UNIQUE KEY `uk_game_player` (`game_id`, `player_id`),
    INDEX `idx_game_id` (`game_id`),
    INDEX `idx_player_id` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏玩家关联表';

-- Token表（用于用户认证）
CREATE TABLE IF NOT EXISTS `user_token` (
    `token` VARCHAR(255) PRIMARY KEY COMMENT 'Token',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `created_at` BIGINT NOT NULL COMMENT '创建时间（时间戳）',
    `expires_at` BIGINT NOT NULL COMMENT '过期时间（时间戳）',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户Token表';

