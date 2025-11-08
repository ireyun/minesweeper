package com.minesweeper.service.imp;

import com.minesweeper.domain.Player;
import com.minesweeper.repository.PlayerRepository;
import com.minesweeper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<Player> findAllPlayers() {
        return playerRepository.getAllPlayers();
    }
}
