package com.minesweeper.game.repository;

import com.minesweeper.game.model.Entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 游戏Repository
 */
@Repository
public interface GameRepository extends JpaRepository<GameEntity, String> {
    /**
     * 根据房间ID查找游戏
     */
    Optional<GameEntity> findByRoomId(String roomId);

    /**
     * 根据房间ID查找所有游戏
     */
    List<GameEntity> findAllByRoomId(String roomId);

    /**
     * 根据游戏状态查找游戏
     */
    List<GameEntity> findByGameStatus(String gameStatus);

    /**
     * 根据玩家ID查找游戏
     */
    List<GameEntity> findByCurrentPlayerId(String playerId);
}

