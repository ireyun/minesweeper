// API基础配置
const API_BASE_URL = '/api';

// 存储token
let authToken = localStorage.getItem('authToken') || '';
let currentUserId = localStorage.getItem('userId') || '';

// API请求封装
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // 添加认证token
    if (authToken) {
        defaultOptions.headers['Authorization'] = `Bearer ${authToken}`;
    }

    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...(options.headers || {}),
        },
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, finalOptions);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || '请求失败');
        }
        
        return data;
    } catch (error) {
        console.error('API请求错误:', error);
        throw error;
    }
}

// 用户API
const UserAPI = {
    // 注册
    async register(username, password, email) {
        const data = await apiRequest('/user/register', {
            method: 'POST',
            body: JSON.stringify({ username, password, email }),
        });
        return data.data;
    },

    // 登录
    async login(username, password) {
        const data = await apiRequest('/user/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
        });
        const loginData = data.data;
        authToken = loginData.token;
        currentUserId = loginData.userId;
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('userId', currentUserId);
        return loginData;
    },

    // 登出
    async logout() {
        try {
            await apiRequest('/user/logout', {
                method: 'POST',
            });
        } finally {
            authToken = '';
            currentUserId = '';
            localStorage.removeItem('authToken');
            localStorage.removeItem('userId');
        }
    },

    // 获取用户信息
    async getUserInfo() {
        const data = await apiRequest('/user/info', {
            method: 'GET',
        });
        return data.data;
    },
};

// 游戏API
const GameAPI = {
    // 创建游戏
    async createGame(width, height, mineCount, difficulty) {
        const data = await apiRequest('/game/create', {
            method: 'POST',
            body: JSON.stringify({
                width,
                height,
                mineCount,
                difficulty,
                userId: currentUserId,
            }),
        });
        return data.data;
    },

    // 获取游戏状态
    async getGameState(gameId) {
        const data = await apiRequest(`/game/${gameId}`, {
            method: 'GET',
        });
        return data.data;
    },

    // 玩家操作
    async playerAction(gameId, row, col, action) {
        const data = await apiRequest('/game/action', {
            method: 'POST',
            body: JSON.stringify({
                gameId,
                row,
                col,
                action,
                userId: currentUserId,
            }),
        });
        return data.data;
    },

    // 暂停游戏
    async pauseGame(gameId) {
        const data = await apiRequest(`/game/${gameId}/pause`, {
            method: 'POST',
        });
        return data.data;
    },

    // 恢复游戏
    async resumeGame(gameId) {
        const data = await apiRequest(`/game/${gameId}/resume`, {
            method: 'POST',
        });
        return data.data;
    },

    // 重启游戏
    async restartGame(gameId) {
        const data = await apiRequest(`/game/${gameId}/restart`, {
            method: 'POST',
        });
        return data.data;
    },

    // 放弃游戏
    async surrenderGame(gameId) {
        const data = await apiRequest(`/game/${gameId}/surrender`, {
            method: 'POST',
        });
        return data.data;
    },
};

// 房间API
const RoomAPI = {
    // 创建房间
    async createRoom(roomName, maxPlayers) {
        const data = await apiRequest('/rooms', {
            method: 'POST',
            body: JSON.stringify({
                roomName,
                maxPlayers,
                hostId: currentUserId,
            }),
        });
        return data.data;
    },

    // 获取房间列表
    async getRooms() {
        const data = await apiRequest('/rooms', {
            method: 'GET',
        });
        return data.data;
    },

    // 获取房间信息
    async getRoom(roomId) {
        const data = await apiRequest(`/rooms/${roomId}`, {
            method: 'GET',
        });
        return data.data;
    },

    // 加入房间
    async joinRoom(roomId) {
        const data = await apiRequest(`/rooms/${roomId}/join`, {
            method: 'POST',
        });
        return data.data;
    },

    // 在房间中创建游戏
    async createGameInRoom(roomId, width, height, mineCount, difficulty) {
        const data = await apiRequest(`/rooms/${roomId}/create-game`, {
            method: 'POST',
            body: JSON.stringify({
                width,
                height,
                mineCount,
                difficulty,
                userId: currentUserId,
                roomId: roomId,
            }),
        });
        return data.data;
    },
};

