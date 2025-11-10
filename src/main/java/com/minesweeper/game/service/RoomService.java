package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.Room;
import com.minesweeper.game.model.Request.CreateRoomRequest;
import com.minesweeper.game.model.Response.JoinRoomResponse;
import com.minesweeper.game.model.Response.RoomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 房间服务类
 */
@Service
public class RoomService {
    // 使用内存存储房间数据（后续可以替换为Repository）
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Autowired
    private UserService userService;

    /**
     * 创建房间
     */
    public RoomResponse createRoom(CreateRoomRequest request) {
        // 验证房主是否存在
        if (userService.getUserById(request.getHostId()) == null) {
            throw new IllegalArgumentException("房主不存在");
        }

        // 生成房间ID
        String roomId = generateRoomId();

        // 获取房主用户名
        String hostUsername = userService.getUserById(request.getHostId()).getUsername();

        // 创建房间实体
        Room room = new Room(roomId, request.getRoomName(), request.getHostId(), 
                            hostUsername, request.getMaxPlayers());

        // 保存房间
        rooms.put(roomId, room);

        // 转换为响应对象
        return convertToRoomResponse(room);
    }

    /**
     * 加入房间
     */
    public JoinRoomResponse joinRoom(String roomId, String userId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        if (room.isFull()) {
            throw new IllegalStateException("房间已满");
        }
        if (!room.getStatus().equals("WAITING")) {
            throw new IllegalStateException("房间不在等待状态，无法加入");
        }

        // 验证用户是否存在
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String username = userService.getUserById(userId).getUsername();

        // 添加玩家
        boolean success = room.addPlayer(userId, username);
        if (!success) {
            throw new IllegalStateException("加入房间失败，玩家可能已在房间中");
        }

        // 转换为响应对象
        JoinRoomResponse response = convertToJoinRoomResponse(room);
        response.setMessage("成功加入房间");
        return response;
    }

    /**
     * 离开房间
     */
    public void leaveRoom(String roomId, String userId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }

        room.removePlayer(userId);

        // 如果房间为空，删除房间
        if (room.getPlayerIds().isEmpty()) {
            rooms.remove(roomId);
        } else {
            // 如果房主离开，转移房主权限给第一个玩家
            if (room.getHostId().equals(userId)) {
                room.setHostId(room.getPlayerIds().get(0));
                room.setHostUsername(room.getPlayerUsernames().get(0));
            }
        }
    }

    /**
     * 获取房间信息
     */
    public RoomResponse getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        return convertToRoomResponse(room);
    }

    /**
     * 获取所有房间列表
     */
    public List<RoomResponse> getAllRooms() {
        List<RoomResponse> roomList = new ArrayList<>();
        for (Room room : rooms.values()) {
            roomList.add(convertToRoomResponse(room));
        }
        return roomList;
    }

    /**
     * 设置房间的游戏ID
     */
    public void setRoomGame(String roomId, String gameId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("房间不存在");
        }
        room.setCurrentGameId(gameId);
        room.setStatus("PLAYING");
    }

    /**
     * 将Room实体转换为RoomResponse
     */
    private RoomResponse convertToRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomName(room.getRoomName());
        response.setHostId(room.getHostId());
        response.setHostUsername(room.getHostUsername());
        response.setPlayerIds(new ArrayList<>(room.getPlayerIds()));
        response.setPlayerUsernames(new ArrayList<>(room.getPlayerUsernames()));
        response.setMaxPlayers(room.getMaxPlayers());
        response.setCurrentPlayerCount(room.getCurrentPlayerCount());
        response.setCurrentGameId(room.getCurrentGameId());
        response.setStatus(room.getStatus());
        response.setCreatedAt(room.getCreatedAt());
        return response;
    }

    /**
     * 将Room实体转换为JoinRoomResponse
     */
    private JoinRoomResponse convertToJoinRoomResponse(Room room) {
        JoinRoomResponse response = new JoinRoomResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomName(room.getRoomName());
        response.setHostId(room.getHostId());
        response.setHostUsername(room.getHostUsername());
        response.setPlayerIds(new ArrayList<>(room.getPlayerIds()));
        response.setPlayerUsernames(new ArrayList<>(room.getPlayerUsernames()));
        response.setMaxPlayers(room.getMaxPlayers());
        response.setCurrentPlayerCount(room.getCurrentPlayerCount());
        response.setCurrentGameId(room.getCurrentGameId());
        response.setStatus(room.getStatus());
        return response;
    }

    /**
     * 生成房间ID
     */
    private String generateRoomId() {
        return "ROOM_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    /**
     * 删除房间
     */
    public void deleteRoom(String roomId) {
        rooms.remove(roomId);
    }
}
