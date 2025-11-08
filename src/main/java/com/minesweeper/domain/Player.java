package com.minesweeper.domain;

// TODO: 这是一个领域对象，测试用

import java.time.Duration;

public class Player {
    long playerId;
    String playerName;
    int wins;
    int losses;
    Duration fastestTime;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public Duration getFastestTime() {
        return fastestTime;
    }

    public void setFastestTime(Duration fastestTime) {
        this.fastestTime = fastestTime;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", wins=" + wins +
                ", losses=" + losses +
                ", fastestTime=" + fastestTime +
                '}';
    }
}
