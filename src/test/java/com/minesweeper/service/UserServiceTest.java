package com.minesweeper.service;

import com.minesweeper.domain.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationContext context;

    @Test
    public void testFindAllPlayers() {
        List<Player> players = userService.findAllPlayers();
        players.forEach(System.out::println);
    }
}
