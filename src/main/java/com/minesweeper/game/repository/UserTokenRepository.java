package com.minesweeper.game.repository;

import com.minesweeper.game.model.Entity.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户Token Repository
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, String> {
    /**
     * 根据用户ID查找Token
     */
    Optional<UserTokenEntity> findByUserId(String userId);

    /**
     * 根据用户ID删除所有Token
     */
    void deleteByUserId(String userId);

    /**
     * 删除过期的Token
     */
    void deleteByExpiresAtLessThan(Long currentTime);

    /**
     * 查找所有过期的Token
     */
    List<UserTokenEntity> findByExpiresAtLessThan(Long currentTime);
}

