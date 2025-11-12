package com.minesweeper.game.model.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 房间实体类（JPA）
 */
@Entity
@Table(name = "room")
public class RoomEntity {
    @Id
    @Column(name = "room_id", length = 64)
    private String roomId;

    @Column(name = "room_name", length = 100, nullable = false)
    private String roomName;

    @Column(name = "host_id", length = 64, nullable = false)
    private String hostId;

    @Column(name = "host_username", length = 50, nullable = false)
    private String hostUsername;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "current_game_id", length = 64)
    private String currentGameId;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "WAITING";

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RoomPlayerEntity> roomPlayers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
        if (status == null) {
            status = "WAITING";
        }
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public List<RoomPlayerEntity> getRoomPlayers() {
        return roomPlayers;
    }

    public void setRoomPlayers(List<RoomPlayerEntity> roomPlayers) {
        this.roomPlayers = roomPlayers;
    }

    /**
     * 获取玩家ID列表
     */
    public List<String> getPlayerIds() {
        List<String> playerIds = new ArrayList<>();
        if (roomPlayers != null) {
            for (RoomPlayerEntity roomPlayer : roomPlayers) {
                playerIds.add(roomPlayer.getPlayerId());
            }
        }
        return playerIds;
    }

    /**
     * 获取玩家用户名列表
     */
    public List<String> getPlayerUsernames() {
        List<String> playerUsernames = new ArrayList<>();
        if (roomPlayers != null) {
            for (RoomPlayerEntity roomPlayer : roomPlayers) {
                playerUsernames.add(roomPlayer.getPlayerUsername());
            }
        }
        return playerUsernames;
    }

    /**
     * 添加玩家
     */
    public boolean addPlayer(String playerId, String playerUsername) {
        if (roomPlayers == null) {
            roomPlayers = new ArrayList<>();
        }
        
        // 检查玩家是否已在房间中
        for (RoomPlayerEntity roomPlayer : roomPlayers) {
            if (roomPlayer.getPlayerId().equals(playerId)) {
                return false; // 玩家已在房间中
            }
        }
        
        // 检查房间是否已满
        if (roomPlayers.size() >= maxPlayers) {
            return false; // 房间已满
        }
        
        // 检查房间状态
        if (!status.equals("WAITING")) {
            return false; // 房间不在等待状态
        }
        
        // 添加玩家
        RoomPlayerEntity roomPlayer = new RoomPlayerEntity();
        roomPlayer.setRoomId(this.roomId);
        roomPlayer.setPlayerId(playerId);
        roomPlayer.setPlayerUsername(playerUsername);
        roomPlayers.add(roomPlayer);
        
        return true;
    }

    /**
     * 移除玩家
     */
    public boolean removePlayer(String playerId) {
        if (roomPlayers == null) {
            return false;
        }
        
        return roomPlayers.removeIf(rp -> rp.getPlayerId().equals(playerId));
    }

    /**
     * 检查房间是否已满
     */
    public boolean isFull() {
        if (roomPlayers == null) {
            return false;
        }
        return roomPlayers.size() >= maxPlayers;
    }

    /**
     * 获取当前玩家数量
     */
    public Integer getCurrentPlayerCount() {
        if (roomPlayers == null) {
            return 0;
        }
        return roomPlayers.size();
    }
}

