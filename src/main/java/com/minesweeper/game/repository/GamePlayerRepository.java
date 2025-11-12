package com.minesweeper.game.repository;

import com.minesweeper.game.model.Entity.GamePlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 游戏玩家Repository
 */
@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayerEntity, Long> {
    /**
     * 根据游戏ID查找所有玩家
     */
    List<GamePlayerEntity> findByGameId(String gameId);

    /**
     * 根据玩家ID查找游戏
     */
    List<GamePlayerEntity> findByPlayerId(String playerId);

    /**
     * 检查玩家是否在游戏中
     */
    boolean existsByGameIdAndPlayerId(String gameId, String playerId);

    /**
     * 根据游戏ID删除所有玩家
     */
    void deleteByGameId(String gameId);
}

