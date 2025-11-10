// 游戏状态
let currentGame = null;
let gameBoard = [];
let gameTimer = null;
let gameStartTime = null;
let isPaused = false;

// 初始化游戏棋盘
function initGameBoard(gameState) {
    currentGame = gameState;
    gameBoard = [];
    const board = gameState.board;
    const width = gameState.width;
    const height = gameState.height;

    // 创建棋盘DOM
    const boardElement = document.getElementById('game-board');
    boardElement.style.gridTemplateColumns = `repeat(${width}, 30px)`;
    boardElement.innerHTML = '';

    // 创建格子
    for (let row = 0; row < height; row++) {
        gameBoard[row] = [];
        for (let col = 0; col < width; col++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.row = row;
            cell.dataset.col = col;
            
            const value = board[row][col];
            updateCell(cell, value, row, col, gameState);
            
            // 添加点击事件
            cell.addEventListener('click', () => handleCellClick(row, col));
            cell.addEventListener('contextmenu', (e) => {
                e.preventDefault();
                handleCellRightClick(row, col);
            });

            boardElement.appendChild(cell);
            gameBoard[row][col] = cell;
        }
    }

    // 显示游戏信息
    document.getElementById('game-info').style.display = 'flex';
    document.getElementById('game-status').textContent = getStatusText(gameState.gameStatus);
    document.getElementById('game-mines-count').textContent = gameState.mineCount;
    
    // 更新游戏时间
    if (gameState.elapsedTime) {
        document.getElementById('game-time').textContent = Math.floor(gameState.elapsedTime / 1000);
    } else {
        document.getElementById('game-time').textContent = '0';
    }
    
    // 开始计时
    if (gameState.gameStatus === 'PLAYING') {
        isPaused = false;
        startGameTimer();
    } else {
        stopGameTimer();
    }

    updateGameMessage('');
}

// 更新格子显示
function updateCell(cell, value, row, col, gameState) {
    cell.className = 'cell';
    cell.textContent = '';

    if (value === -2) {
        // 未揭示的格子
        cell.className = 'cell';
    } else if (value === -3) {
        // 标记的格子
        cell.className = 'cell flagged';
        cell.textContent = '🚩';
    } else if (value === -1) {
        // 地雷
        cell.className = 'cell mine';
        cell.textContent = '💣';
    } else {
        // 数字
        cell.className = `cell revealed number-${value}`;
        if (value > 0) {
            cell.textContent = value;
        }
    }
}

// 更新整个棋盘
function updateGameBoard(gameState) {
    currentGame = gameState;
    const board = gameState.board;
    const width = gameState.width;
    const height = gameState.height;

    for (let row = 0; row < height; row++) {
        for (let col = 0; col < width; col++) {
            const cell = gameBoard[row][col];
            const value = board[row][col];
            updateCell(cell, value, row, col, gameState);
        }
    }

    // 更新游戏状态
    document.getElementById('game-status').textContent = getStatusText(gameState.gameStatus);
    
    // 更新游戏时间
    if (gameState.elapsedTime !== undefined) {
        document.getElementById('game-time').textContent = Math.floor(gameState.elapsedTime / 1000);
    }
    
    // 处理游戏结束
    if (gameState.gameStatus === 'WON') {
        stopGameTimer();
        updateGameMessage('恭喜！你赢了！', 'success');
        document.getElementById('pause-btn').style.display = 'none';
        document.getElementById('resume-btn').style.display = 'none';
    } else if (gameState.gameStatus === 'LOST') {
        stopGameTimer();
        updateGameMessage('游戏结束！你踩到地雷了！', 'error');
        document.getElementById('pause-btn').style.display = 'none';
        document.getElementById('resume-btn').style.display = 'none';
    } else if (gameState.gameStatus === 'SURRENDERED') {
        stopGameTimer();
        updateGameMessage('游戏已放弃', 'info');
        document.getElementById('pause-btn').style.display = 'none';
        document.getElementById('resume-btn').style.display = 'none';
    } else if (gameState.gameStatus === 'PAUSED') {
        stopGameTimer();
        isPaused = true;
        document.getElementById('pause-btn').style.display = 'none';
        document.getElementById('resume-btn').style.display = 'inline-block';
    } else if (gameState.gameStatus === 'PLAYING') {
        document.getElementById('pause-btn').style.display = 'inline-block';
        document.getElementById('resume-btn').style.display = 'none';
        if (!isPaused) {
            startGameTimer();
        }
    }
}

// 处理格子点击
async function handleCellClick(row, col) {
    if (!currentGame || currentGame.gameStatus !== 'PLAYING') {
        return;
    }

    try {
        const gameState = await GameAPI.playerAction(currentGame.gameId, row, col, 'CLICK');
        updateGameBoard(gameState);
    } catch (error) {
        updateGameMessage('操作失败: ' + error.message, 'error');
    }
}

// 处理格子右键点击（标记）
async function handleCellRightClick(row, col) {
    if (!currentGame || currentGame.gameStatus !== 'PLAYING') {
        return;
    }

    try {
        const cell = gameBoard[row][col];
        const isFlagged = cell.classList.contains('flagged');
        const action = isFlagged ? 'UNFLAG' : 'FLAG';
        
        const gameState = await GameAPI.playerAction(currentGame.gameId, row, col, action);
        updateGameBoard(gameState);
    } catch (error) {
        updateGameMessage('操作失败: ' + error.message, 'error');
    }
}

// 创建游戏
async function createGame() {
    const width = parseInt(document.getElementById('game-width').value);
    const height = parseInt(document.getElementById('game-height').value);
    const mines = parseInt(document.getElementById('game-mines').value);
    const difficulty = document.getElementById('game-difficulty').value;

    try {
        const gameState = await GameAPI.createGame(width, height, mines, difficulty);
        hideCreateGameDialog();
        initGameBoard(gameState);
        updateGameMessage('游戏开始！', 'info');
    } catch (error) {
        updateGameMessage('创建游戏失败: ' + error.message, 'error');
    }
}

// 暂停游戏
async function pauseGame() {
    if (!currentGame) return;

    try {
        const gameState = await GameAPI.pauseGame(currentGame.gameId);
        updateGameBoard(gameState);
        isPaused = true;
        updateGameMessage('游戏已暂停', 'info');
    } catch (error) {
        updateGameMessage('暂停失败: ' + error.message, 'error');
    }
}

// 恢复游戏
async function resumeGame() {
    if (!currentGame) return;

    try {
        const gameState = await GameAPI.resumeGame(currentGame.gameId);
        updateGameBoard(gameState);
        isPaused = false;
        updateGameMessage('游戏继续', 'info');
    } catch (error) {
        updateGameMessage('恢复失败: ' + error.message, 'error');
    }
}

// 重启游戏
async function restartGame() {
    if (!currentGame) return;

    if (!confirm('确定要重启游戏吗？')) {
        return;
    }

    try {
        const gameState = await GameAPI.restartGame(currentGame.gameId);
        initGameBoard(gameState);
        updateGameMessage('游戏已重启', 'info');
    } catch (error) {
        updateGameMessage('重启失败: ' + error.message, 'error');
    }
}

// 放弃游戏
async function surrenderGame() {
    if (!currentGame) return;

    if (!confirm('确定要放弃游戏吗？')) {
        return;
    }

    try {
        const gameState = await GameAPI.surrenderGame(currentGame.gameId);
        updateGameBoard(gameState);
    } catch (error) {
        updateGameMessage('放弃失败: ' + error.message, 'error');
    }
}

// 游戏计时器
function startGameTimer() {
    if (gameTimer) {
        clearInterval(gameTimer);
    }

    // 使用游戏开始时间计算
    if (currentGame && currentGame.startTime) {
        gameStartTime = currentGame.startTime;
    } else {
        gameStartTime = Date.now();
    }

    // 初始显示时间
    if (currentGame && currentGame.elapsedTime) {
        document.getElementById('game-time').textContent = Math.floor(currentGame.elapsedTime / 1000);
    }

    gameTimer = setInterval(() => {
        if (!isPaused && currentGame && currentGame.gameStatus === 'PLAYING') {
            if (currentGame.elapsedTime) {
                // 使用后端返回的时间
                const elapsed = Math.floor(currentGame.elapsedTime / 1000);
                document.getElementById('game-time').textContent = elapsed;
                // 每秒递增（因为后端时间可能不是实时更新的）
                currentGame.elapsedTime += 1000;
            } else {
                // 前端计算时间
                const elapsed = Math.floor((Date.now() - gameStartTime) / 1000);
                document.getElementById('game-time').textContent = elapsed;
            }
        }
    }, 1000);
}

function stopGameTimer() {
    if (gameTimer) {
        clearInterval(gameTimer);
        gameTimer = null;
    }
}

// 获取状态文本
function getStatusText(status) {
    const statusMap = {
        'PLAYING': '游戏中',
        'WON': '胜利',
        'LOST': '失败',
        'PAUSED': '已暂停',
        'SURRENDERED': '已放弃',
    };
    return statusMap[status] || status;
}

// 更新游戏消息
function updateGameMessage(message, type = '') {
    const messageElement = document.getElementById('game-message');
    messageElement.textContent = message;
    messageElement.className = 'game-message';
    if (type) {
        messageElement.classList.add(type);
    }
    
    if (message) {
        setTimeout(() => {
            messageElement.textContent = '';
            messageElement.className = 'game-message';
        }, 3000);
    }
}

// 显示创建游戏对话框
function showCreateGameDialog() {
    document.getElementById('create-game-dialog').style.display = 'flex';
}

// 隐藏创建游戏对话框
function hideCreateGameDialog() {
    document.getElementById('create-game-dialog').style.display = 'none';
}

