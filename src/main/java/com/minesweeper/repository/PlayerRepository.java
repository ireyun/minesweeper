package com.minesweeper.repository;

import com.minesweeper.domain.Player;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// TODO 玩家数据持久层操作，与数据库打交道，Mapper的作用是让Spring识别到该代理
@Mapper
public interface PlayerRepository {
    @Select("select * from player")
    List<Player> getAllPlayers();
}
