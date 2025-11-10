# 扫雷游戏项目

## 项目结构

```
minesweeper/
├── src/main/java/com/minesweeper/
│   ├── Main.java                    # Spring Boot主类
│   ├── api_gateway/                 # API网关配置
│   └── game/                        # 游戏模块
│       ├── controller/              # 控制器层
│       ├── service/                 # 服务层
│       ├── model/                   # 数据模型
│       │   ├── Entity/             # 实体类
│       │   ├── Request/            # 请求模型
│       │   └── Response/           # 响应模型
│       └── exception/               # 异常处理
└── src/main/resources/
    └── static/                      # 前端静态资源
        ├── index.html              # 主页面
        ├── css/                    # 样式文件
        └── js/                     # JavaScript文件
```

## 运行项目

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 运行项目
```bash
mvn spring-boot:run
```

或者直接运行 `Main.java`

### 3. 访问前端
打开浏览器访问: `http://localhost:8080`

## 功能特性

### 用户功能
- 用户注册
- 用户登录
- 用户登出

### 游戏功能
- 创建游戏（自定义难度）
- 点击格子揭示
- 右键标记地雷
- 暂停/继续游戏
- 重启游戏
- 放弃游戏
- 实时显示游戏状态和时间

## API端点

### 用户API
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户登出
- `GET /api/user/info` - 获取用户信息

### 游戏API
- `POST /api/game/create` - 创建游戏
- `GET /api/game/{gameId}` - 获取游戏状态
- `POST /api/game/action` - 玩家操作
- `POST /api/game/{gameId}/pause` - 暂停游戏
- `POST /api/game/{gameId}/resume` - 恢复游戏
- `POST /api/game/{gameId}/restart` - 重启游戏
- `POST /api/game/{gameId}/surrender` - 放弃游戏

### 房间API
- `POST /api/rooms` - 创建房间
- `GET /api/rooms` - 获取房间列表
- `GET /api/rooms/{roomId}` - 获取房间信息
- `POST /api/rooms/{roomId}/join` - 加入房间
- `POST /api/rooms/{roomId}/leave` - 离开房间

## 测试步骤

1. **启动项目**
   - 运行 `Main.java` 或使用 `mvn spring-boot:run`
   - 服务器将在 `http://localhost:8080` 启动

2. **访问前端**
   - 打开浏览器访问 `http://localhost:8080`
   - 你将看到登录/注册界面

3. **注册用户**
   - 点击"注册"标签
   - 输入用户名和密码（邮箱可选）
   - 点击"注册"按钮

4. **登录**
   - 输入用户名和密码
   - 点击"登录"按钮

5. **创建游戏**
   - 登录后，点击"创建游戏"按钮
   - 设置游戏参数（宽度、高度、地雷数、难度）
   - 点击"开始游戏"

6. **玩游戏**
   - 左键点击格子揭示
   - 右键点击格子标记/取消标记地雷
   - 使用控制按钮暂停、重启或放弃游戏

## 技术栈

- **后端**: Spring Boot 2.7.18, Java 8
- **前端**: HTML5, CSS3, JavaScript (ES6+)
- **存储**: 内存存储（ConcurrentHashMap）

## 注意事项

- 当前使用内存存储，服务器重启后数据会丢失
- Token认证机制较为简单，生产环境建议使用JWT
- 密码未加密，生产环境应使用BCrypt等加密算法
- CORS配置允许所有来源，生产环境应限制特定域名

## 后续优化建议

1. 添加数据库支持（MySQL/PostgreSQL）
2. 实现JWT Token认证
3. 添加密码加密（BCrypt）
4. 添加单元测试
5. 添加API文档（Swagger）
6. 优化前端UI/UX
7. 添加游戏统计功能
8. 实现多人游戏功能

