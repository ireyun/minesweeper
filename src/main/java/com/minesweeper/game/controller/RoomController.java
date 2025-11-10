package com.minesweeper.game.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;


    // 1. 创建房间
    //POST /api/rooms
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {
        // 实现...
    }

    // 2. 加入房间
    //POST /api/rooms/{roomId}/join
    @PostMapping("/{roomId}/join")
    public ResponseEntity<ApiResponse<JoinRoomResponse>> joinRoom(
            @PathVariable String roomId) {
        // 实现...
    }

}
