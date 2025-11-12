package com.minesweeper.game.service;

import com.minesweeper.game.model.Entity.RoomEntity;
import com.minesweeper.game.model.Entity.RoomPlayerEntity;
import com.minesweeper.game.model.Entity.UserEntity;
import com.minesweeper.game.model.Request.CreateRoomRequest;
import com.minesweeper.game.model.Response.JoinRoomResponse;
import com.minesweeper.game.model.Response.RoomResponse;
import com.minesweeper.game.repository.RoomPlayerRepository;
import com.minesweeper.game.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 房间服务类（使用数据库）
 */
@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomPlayerRepository roomPlayerRepository;
    
    @Autowired
    private UserService userService;
    
    private final Random random = new Random();

    /**
     * 创建房间
     */
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        // 验证房主是否存在
        UserEntity host = userService.getUserById(request.getHostId());
        if (host == null) {
            throw new IllegalArgumentException("房主不存在");
        }

        // 生成房间ID
        String roomId = generateRoomId();

        // 创建房间实体
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setRoomId(roomId);
        roomEntity.setRoomName(request.getRoomName());
        roomEntity.setHostId(request.getHostId());
        roomEntity.setHostUsername(host.getUsername());
        roomEntity.setMaxPlayers(request.getMaxPlayers());
        roomEntity.setStatus("WAITING");

        // 保存房间
        roomEntity = roomRepository.save(roomEntity);

        // 添加房主为第一个玩家
        RoomPlayerEntity hostPlayer = new RoomPlayerEntity();
        hostPlayer.setRoomId(roomId);
        hostPlayer.setPlayerId(request.getHostId());
        hostPlayer.setPlayerUsername(host.getUsername());
        roomPlayerRepository.save(hostPlayer);

        // 刷新实体以获取关联数据
        roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间创建失败"));

        // 转换为响应对象
        return convertToRoomResponse(roomEntity);
    }

    /**
     * 加入房间
     */
    @Transactional
    public JoinRoomResponse joinRoom(String roomId, String userId) {
        // 查找房间
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        
        // 检查房间是否已满
        long currentCount = roomPlayerRepository.countByRoomId(roomId);
        if (currentCount >= roomEntity.getMaxPlayers()) {
            throw new IllegalStateException("房间已满");
        }
        
        // 检查房间状态
        if (!roomEntity.getStatus().equals("WAITING")) {
            throw new IllegalStateException("房间不在等待状态，无法加入");
        }

        // 验证用户是否存在
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查玩家是否已在房间中
        if (roomPlayerRepository.existsByRoomIdAndPlayerId(roomId, userId)) {
            throw new IllegalStateException("玩家已在房间中");
        }

        // 添加玩家
        RoomPlayerEntity roomPlayer = new RoomPlayerEntity();
        roomPlayer.setRoomId(roomId);
        roomPlayer.setPlayerId(userId);
        roomPlayer.setPlayerUsername(user.getUsername());
        roomPlayerRepository.save(roomPlayer);

        // 刷新实体
        roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));

        // 转换为响应对象
        JoinRoomResponse response = convertToJoinRoomResponse(roomEntity);
        response.setMessage("成功加入房间");
        return response;
    }

    /**
     * 离开房间
     */
    @Transactional
    public void leaveRoom(String roomId, String userId) {
        // 查找房间
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));

        // 移除玩家
        roomPlayerRepository.deleteByRoomIdAndPlayerId(roomId, userId);

        // 检查房间是否为空
        long remainingCount = roomPlayerRepository.countByRoomId(roomId);
        if (remainingCount == 0) {
            // 删除房间
            roomRepository.deleteById(roomId);
        } else {
            // 如果房主离开，转移房主权限给第一个玩家
            if (roomEntity.getHostId().equals(userId)) {
                List<RoomPlayerEntity> remainingPlayers = roomPlayerRepository.findByRoomId(roomId);
                if (!remainingPlayers.isEmpty()) {
                    RoomPlayerEntity newHost = remainingPlayers.get(0);
                    roomEntity.setHostId(newHost.getPlayerId());
                    roomEntity.setHostUsername(newHost.getPlayerUsername());
                    roomRepository.save(roomEntity);
                }
            }
        }
    }

    /**
     * 获取房间信息
     */
    public RoomResponse getRoom(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        return convertToRoomResponse(roomEntity);
    }

    /**
     * 获取所有房间列表
     */
    public List<RoomResponse> getAllRooms() {
        List<RoomEntity> rooms = roomRepository.findAll();
        List<RoomResponse> roomList = new ArrayList<>();
        for (RoomEntity roomEntity : rooms) {
            roomList.add(convertToRoomResponse(roomEntity));
        }
        return roomList;
    }

    /**
     * 设置房间的游戏ID
     */
    @Transactional
    public void setRoomGame(String roomId, String gameId) {
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        roomEntity.setCurrentGameId(gameId);
        roomEntity.setStatus("PLAYING");
        roomRepository.save(roomEntity);
    }

    /**
     * 将RoomEntity转换为RoomResponse
     */
    private RoomResponse convertToRoomResponse(RoomEntity roomEntity) {
        RoomResponse response = new RoomResponse();
        response.setRoomId(roomEntity.getRoomId());
        response.setRoomName(roomEntity.getRoomName());
        response.setHostId(roomEntity.getHostId());
        response.setHostUsername(roomEntity.getHostUsername());
        response.setMaxPlayers(roomEntity.getMaxPlayers());
        response.setCurrentGameId(roomEntity.getCurrentGameId());
        response.setStatus(roomEntity.getStatus());
        response.setCreatedAt(roomEntity.getCreatedAt());
        
        // 获取玩家列表
        List<RoomPlayerEntity> roomPlayers = roomPlayerRepository.findByRoomId(roomEntity.getRoomId());
        List<String> playerIds = new ArrayList<>();
        List<String> playerUsernames = new ArrayList<>();
        for (RoomPlayerEntity roomPlayer : roomPlayers) {
            playerIds.add(roomPlayer.getPlayerId());
            playerUsernames.add(roomPlayer.getPlayerUsername());
        }
        response.setPlayerIds(playerIds);
        response.setPlayerUsernames(playerUsernames);
        response.setCurrentPlayerCount(playerIds.size());
        
        return response;
    }

    /**
     * 将RoomEntity转换为JoinRoomResponse
     */
    private JoinRoomResponse convertToJoinRoomResponse(RoomEntity roomEntity) {
        JoinRoomResponse response = new JoinRoomResponse();
        response.setRoomId(roomEntity.getRoomId());
        response.setRoomName(roomEntity.getRoomName());
        response.setHostId(roomEntity.getHostId());
        response.setHostUsername(roomEntity.getHostUsername());
        response.setMaxPlayers(roomEntity.getMaxPlayers());
        response.setCurrentGameId(roomEntity.getCurrentGameId());
        response.setStatus(roomEntity.getStatus());
        
        // 获取玩家列表
        List<RoomPlayerEntity> roomPlayers = roomPlayerRepository.findByRoomId(roomEntity.getRoomId());
        List<String> playerIds = new ArrayList<>();
        List<String> playerUsernames = new ArrayList<>();
        for (RoomPlayerEntity roomPlayer : roomPlayers) {
            playerIds.add(roomPlayer.getPlayerId());
            playerUsernames.add(roomPlayer.getPlayerUsername());
        }
        response.setPlayerIds(playerIds);
        response.setPlayerUsernames(playerUsernames);
        response.setCurrentPlayerCount(playerIds.size());
        
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
    @Transactional
    public void deleteRoom(String roomId) {
        // 先删除所有玩家关联
        roomPlayerRepository.deleteByRoomId(roomId);
        // 再删除房间
        roomRepository.deleteById(roomId);
    }
}
