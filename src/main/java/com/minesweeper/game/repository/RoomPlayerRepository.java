package com.minesweeper.game.repository;

import com.minesweeper.game.model.Entity.RoomPlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房间玩家Repository
 */
@Repository
public interface RoomPlayerRepository extends JpaRepository<RoomPlayerEntity, Long> {
    /**
     * 根据房间ID查找所有玩家
     */
    List<RoomPlayerEntity> findByRoomId(String roomId);

    /**
     * 根据玩家ID查找房间
     */
    List<RoomPlayerEntity> findByPlayerId(String playerId);

    /**
     * 根据房间ID和玩家ID查找
     */
    Optional<RoomPlayerEntity> findByRoomIdAndPlayerId(String roomId, String playerId);

    /**
     * 检查玩家是否在房间中
     */
    boolean existsByRoomIdAndPlayerId(String roomId, String playerId);

    /**
     * 根据房间ID删除所有玩家
     */
    void deleteByRoomId(String roomId);

    /**
     * 根据房间ID和玩家ID删除
     */
    void deleteByRoomIdAndPlayerId(String roomId, String playerId);

    /**
     * 统计房间中的玩家数量
     */
    long countByRoomId(String roomId);
}

