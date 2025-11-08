package com.minesweeper.controler;


import com.minesweeper.domain.Player;
import com.minesweeper.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserControl {
    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping
    public List<Player> findAllPlayers() {
        return playerRepository.getAllPlayers();
    }
}
