package com.minesweeper.game.repository;

import com.minesweeper.game.model.Entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 房间Repository
 */
@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    /**
     * 根据房主ID查找房间
     */
    List<RoomEntity> findByHostId(String hostId);

    /**
     * 根据状态查找房间
     */
    List<RoomEntity> findByStatus(String status);

    /**
     * 根据当前游戏ID查找房间
     */
    Optional<RoomEntity> findByCurrentGameId(String gameId);
}

