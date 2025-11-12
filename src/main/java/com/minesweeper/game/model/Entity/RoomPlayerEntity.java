package com.minesweeper.game.model.Entity;

import javax.persistence.*;

/**
 * 房间玩家关联实体
 */
@Entity
@Table(name = "room_player", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_id", "player_id"})
})
public class RoomPlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "room_id", length = 64, nullable = false)
    private String roomId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "player_username", length = 50, nullable = false)
    private String playerUsername;

    @Column(name = "join_time", nullable = false)
    private Long joinTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private RoomEntity room;

    @PrePersist
    protected void onCreate() {
        if (joinTime == null) {
            joinTime = System.currentTimeMillis();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }
}

