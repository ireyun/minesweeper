package com.minesweeper.service;

import com.minesweeper.domain.Player;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    /**
     * 找到所有的player，并输出
     * @return List<Player>: 查询所有玩家信息
     */
    List<Player> findAllPlayers();
}
