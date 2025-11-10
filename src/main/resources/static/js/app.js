// 应用主逻辑
let currentUser = null;

// 页面加载时检查登录状态
document.addEventListener('DOMContentLoaded', () => {
    checkAuthStatus();
});

// 检查认证状态
async function checkAuthStatus() {
    const token = localStorage.getItem('authToken');
    if (token) {
        try {
            const userInfo = await UserAPI.getUserInfo();
            showGameSection(userInfo.username);
        } catch (error) {
            // Token无效，清除并显示登录界面
            localStorage.removeItem('authToken');
            localStorage.removeItem('userId');
            showAuthSection();
        }
    } else {
        showAuthSection();
    }
}

// 显示登录界面
function showAuthSection() {
    document.getElementById('auth-section').style.display = 'block';
    document.getElementById('room-section').style.display = 'none';
    document.getElementById('game-section').style.display = 'none';
}

// 显示游戏界面
function showGameSection(username) {
    document.getElementById('auth-section').style.display = 'none';
    document.getElementById('room-section').style.display = 'block';
    document.getElementById('game-section').style.display = 'none';
    document.getElementById('username-display').textContent = `用户: ${username}`;
    currentUser = username;
}

// 显示登录表单
function showLogin() {
    document.getElementById('login-form').style.display = 'block';
    document.getElementById('register-form').style.display = 'none';
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    clearErrors();
}

// 显示注册表单
function showRegister() {
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'block';
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    clearErrors();
}

// 清除错误信息
function clearErrors() {
    document.getElementById('login-error').textContent = '';
    document.getElementById('register-error').textContent = '';
}

// 登录
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const errorElement = document.getElementById('login-error');

    if (!username || !password) {
        errorElement.textContent = '请填写用户名和密码';
        return;
    }

    try {
        const loginData = await UserAPI.login(username, password);
        showGameSection(loginData.username);
        errorElement.textContent = '';
    } catch (error) {
        errorElement.textContent = error.message || '登录失败';
    }
}

// 注册
async function register() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const email = document.getElementById('register-email').value;
    const errorElement = document.getElementById('register-error');

    if (!username || !password) {
        errorElement.textContent = '请填写用户名和密码';
        return;
    }

    try {
        const userInfo = await UserAPI.register(username, password, email);
        // 注册成功后自动登录
        const loginData = await UserAPI.login(username, password);
        showGameSection(loginData.username);
        errorElement.textContent = '';
    } catch (error) {
        errorElement.textContent = error.message || '注册失败';
    }
}

// 登出
async function logout() {
    try {
        await UserAPI.logout();
        showAuthSection();
        // 清理游戏状态
        if (typeof stopGameTimer === 'function' && currentGame) {
            stopGameTimer();
            currentGame = null;
            document.getElementById('game-board').innerHTML = '';
            document.getElementById('game-info').style.display = 'none';
        }
        // 清理房间状态
        currentRoom = null;
        if (roomListRefreshTimer) {
            clearInterval(roomListRefreshTimer);
            roomListRefreshTimer = null;
        }
    } catch (error) {
        console.error('登出失败:', error);
        // 即使登出失败，也清除本地状态
        showAuthSection();
    }
}

// 允许回车键登录/注册
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('login-password').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            login();
        }
    });

    document.getElementById('register-password').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            register();
        }
    });
});

