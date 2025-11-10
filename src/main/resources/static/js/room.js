// 房间管理逻辑
let currentRoom = null;
let roomListRefreshTimer = null;

// 显示房间选择界面
function showRoomSection() {
    document.getElementById('auth-section').style.display = 'none';
    document.getElementById('room-section').style.display = 'block';
    document.getElementById('game-section').style.display = 'none';
    hideCreateRoomDialog();
    hideJoinRoomList();
}

// 显示创建房间对话框
function showCreateRoomDialog() {
    document.getElementById('create-room-dialog').style.display = 'flex';
}

// 隐藏创建房间对话框
function hideCreateRoomDialog() {
    document.getElementById('create-room-dialog').style.display = 'none';
}

// 显示加入房间列表
function showJoinRoomList() {
    document.getElementById('room-list-section').style.display = 'block';
    refreshRoomList();
    // 每5秒自动刷新房间列表
    if (roomListRefreshTimer) {
        clearInterval(roomListRefreshTimer);
    }
    roomListRefreshTimer = setInterval(refreshRoomList, 5000);
}

// 隐藏加入房间列表
function hideJoinRoomList() {
    document.getElementById('room-list-section').style.display = 'none';
    if (roomListRefreshTimer) {
        clearInterval(roomListRefreshTimer);
        roomListRefreshTimer = null;
    }
}

// 创建房间并创建游戏
async function createRoomWithGame() {
    const roomName = document.getElementById('room-name').value || '我的房间';
    const maxPlayers = parseInt(document.getElementById('room-max-players').value) || 4;
    const width = parseInt(document.getElementById('room-game-width').value) || 10;
    const height = parseInt(document.getElementById('room-game-height').value) || 10;
    const mines = parseInt(document.getElementById('room-game-mines').value) || 15;
    const difficulty = document.getElementById('room-game-difficulty').value || 'EASY';

    try {
        // 创建房间
        const room = await RoomAPI.createRoom(roomName, maxPlayers);
        currentRoom = room;

        // 在房间中创建游戏
        const gameState = await RoomAPI.createGameInRoom(room.roomId, width, height, mines, difficulty);
        
        // 隐藏对话框
        hideCreateRoomDialog();
        
        // 进入游戏界面
        enterGame(gameState);
    } catch (error) {
        updateRoomMessage('创建房间失败: ' + error.message, 'error');
    }
}

// 刷新房间列表
async function refreshRoomList() {
    try {
        const rooms = await RoomAPI.getRooms();
        displayRoomList(rooms);
    } catch (error) {
        console.error('获取房间列表失败:', error);
        updateRoomMessage('获取房间列表失败: ' + error.message, 'error');
    }
}

// 显示房间列表
function displayRoomList(rooms) {
    const roomListElement = document.getElementById('room-list');
    roomListElement.innerHTML = '';

    if (rooms.length === 0) {
        roomListElement.innerHTML = '<div class="no-rooms">暂无房间</div>';
        return;
    }

    rooms.forEach(room => {
        const roomCard = document.createElement('div');
        roomCard.className = 'room-card';
        
        const canJoin = room.status === 'WAITING' && 
                       room.currentPlayerCount < room.maxPlayers &&
                       !room.playerIds.includes(localStorage.getItem('userId'));

        roomCard.innerHTML = `
            <div class="room-card-header">
                <h3>${room.roomName}</h3>
                <span class="room-status ${room.status.toLowerCase()}">${getRoomStatusText(room.status)}</span>
            </div>
            <div class="room-card-info">
                <div class="room-info-item">
                    <span>房主: ${room.hostUsername}</span>
                </div>
                <div class="room-info-item">
                    <span>玩家: ${room.currentPlayerCount}/${room.maxPlayers}</span>
                </div>
                <div class="room-info-item">
                    <span>玩家列表: ${room.playerUsernames.join(', ')}</span>
                </div>
            </div>
            <div class="room-card-actions">
                ${canJoin ? `<button onclick="joinRoom('${room.roomId}')" class="btn-primary">加入房间</button>` : ''}
                ${room.status === 'PLAYING' && room.currentGameId ? 
                    `<button onclick="joinGameFromRoom('${room.currentGameId}')" class="btn-primary">进入游戏</button>` : ''}
            </div>
        `;

        roomListElement.appendChild(roomCard);
    });
}

// 加入房间
async function joinRoom(roomId) {
    try {
        const joinResponse = await RoomAPI.joinRoom(roomId);
        currentRoom = joinResponse;

        // 如果房间有游戏，直接进入游戏
        if (joinResponse.currentGameId) {
            const gameState = await GameAPI.getGameState(joinResponse.currentGameId);
            enterGame(gameState);
        } else {
            // 否则显示房间信息
            updateRoomMessage('成功加入房间: ' + joinResponse.roomName, 'success');
            // 可以在这里显示房间等待界面
            // 暂时自动刷新房间列表
            refreshRoomList();
        }
    } catch (error) {
        updateRoomMessage('加入房间失败: ' + error.message, 'error');
        refreshRoomList();
    }
}

// 从房间进入游戏
async function joinGameFromRoom(gameId) {
    try {
        const gameState = await GameAPI.getGameState(gameId);
        enterGame(gameState);
    } catch (error) {
        updateRoomMessage('进入游戏失败: ' + error.message, 'error');
    }
}

// 进入游戏界面
function enterGame(gameState) {
    document.getElementById('room-section').style.display = 'none';
    document.getElementById('game-section').style.display = 'block';
    document.getElementById('game-username-display').textContent = `用户: ${currentUser}`;
    
    // 初始化游戏棋盘（如果initGameBoard函数存在）
    if (typeof initGameBoard === 'function') {
        initGameBoard(gameState);
    }
    // 更新游戏消息（如果updateGameMessage函数存在）
    if (typeof updateGameMessage === 'function') {
        updateGameMessage('游戏开始！', 'info');
    }
}

// 返回房间
function backToRoom() {
    if (currentRoom) {
        showRoomSection();
        // 清理游戏状态
        if (currentGame) {
            stopGameTimer();
            currentGame = null;
            document.getElementById('game-board').innerHTML = '';
            document.getElementById('game-info').style.display = 'none';
        }
    } else {
        showRoomSection();
    }
}

// 获取房间状态文本
function getRoomStatusText(status) {
    const statusMap = {
        'WAITING': '等待中',
        'PLAYING': '游戏中',
    };
    return statusMap[status] || status;
}

// 更新房间消息
function updateRoomMessage(message, type = '') {
    // 可以创建一个消息显示区域
    console.log(`[${type}] ${message}`);
    // 或者使用现有的游戏消息区域
    if (document.getElementById('game-message')) {
        updateGameMessage(message, type);
    }
}

