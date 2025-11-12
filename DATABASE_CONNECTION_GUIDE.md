# 数据库连接配置指南

## ✅ 已完成的配置

### 1. 依赖配置
- ✅ 添加了 Spring Data JPA 依赖
- ✅ 添加了 MySQL 驱动依赖
- ✅ 添加了 Jackson JSON 处理依赖

### 2. 数据库配置
- ✅ 配置了数据库连接信息（application.properties）
- ✅ 配置了 JPA/Hibernate 设置
- ✅ 配置了自动表结构更新（ddl-auto=update）

### 3. 数据库表结构
- ✅ 创建了完整的SQL脚本（schema.sql）
- ✅ 定义了6个数据表：
  - `user` - 用户表
  - `room` - 房间表
  - `room_player` - 房间玩家关联表
  - `game` - 游戏表
  - `game_player` - 游戏玩家关联表
  - `user_token` - 用户Token表

### 4. JPA实体类
- ✅ UserEntity - 用户实体
- ✅ RoomEntity - 房间实体
- ✅ RoomPlayerEntity - 房间玩家实体
- ✅ GameEntity - 游戏实体
- ✅ GamePlayerEntity - 游戏玩家实体
- ✅ UserTokenEntity - Token实体

### 5. Repository接口
- ✅ UserRepository
- ✅ RoomRepository
- ✅ RoomPlayerRepository
- ✅ GameRepository
- ✅ GamePlayerRepository
- ✅ UserTokenRepository

### 6. 工具类
- ✅ JsonConverter - JSON转换工具
- ✅ IntegerArray2DConverter - 二维Integer数组转换器
- ✅ BooleanArray2DConverter - 二维boolean数组转换器

### 7. 连接测试
- ✅ DatabaseConfig - 数据库连接测试类（启动时自动测试）

## 🚀 快速开始

### 步骤1: 配置MySQL数据库

1. **创建数据库**
   ```sql
   CREATE DATABASE IF NOT EXISTS minesweeper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **修改数据库连接配置**
   
   编辑 `src/main/resources/application.properties`：
   ```properties
   # 修改为您的MySQL配置
   spring.datasource.url=jdbc:mysql://localhost:3306/minesweeper?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=您的MySQL密码
   ```

### 步骤2: 启动项目

```bash
mvn spring-boot:run
```

### 步骤3: 验证连接

启动后，查看控制台日志：

**✅ 成功连接时：**
```
=========================================
数据库连接成功！
数据库URL: jdbc:mysql://localhost:3306/minesweeper
数据库驱动: MySQL Connector/J
数据库版本: 8.0.xx
=========================================
当前数据库: minesweeper
数据库表数量: 6
数据库连接测试完成
```

**❌ 连接失败时：**
会显示详细的错误信息和解决建议。

### 步骤4: 验证表结构

连接到MySQL，执行：

```sql
USE minesweeper;
SHOW TABLES;
```

应该看到以下6个表：
- user
- room
- room_player
- game
- game_player
- user_token

## 📋 数据库配置说明

### application.properties 配置项

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/minesweeper?...
spring.datasource.username=root
spring.datasource.password=root

# JPA配置
spring.jpa.hibernate.ddl-auto=update  # 自动更新表结构
spring.jpa.show-sql=true              # 显示SQL语句
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 重要配置说明

1. **spring.jpa.hibernate.ddl-auto**
   - `update` - 自动更新表结构（开发环境推荐）
   - `create` - 每次启动都重建表（慎用）
   - `validate` - 只验证表结构（生产环境推荐）
   - `none` - 不做任何操作

2. **时区设置**
   - 连接URL中包含：`serverTimezone=Asia/Shanghai`
   - 确保MySQL和应用的时区一致

3. **字符编码**
   - 数据库使用 `utf8mb4` 字符集
   - 支持emoji和特殊字符

## 🔧 故障排除

### 常见问题

1. **连接被拒绝**
   - 检查MySQL服务是否启动
   - 检查端口是否为3306
   - 检查防火墙设置

2. **访问被拒绝**
   - 检查用户名和密码
   - 检查用户权限
   - 尝试重置MySQL密码

3. **数据库不存在**
   - 创建数据库：`CREATE DATABASE minesweeper;`
   - 或修改application.properties中的数据库名称

4. **时区错误**
   - 在连接URL中添加时区参数
   - 或在MySQL中设置时区

5. **SSL连接错误**
   - 在连接URL中添加：`useSSL=false&allowPublicKeyRetrieval=true`

## 📝 下一步

数据库连接成功后，您可以：

1. **查看自动创建的表结构**
   ```sql
   DESCRIBE user;
   DESCRIBE room;
   DESCRIBE game;
   ```

2. **测试数据操作**
   - 启动应用
   - 使用前端注册用户
   - 查看数据库中的用户数据

3. **修改Service层使用Repository**
   - 当前Service层仍使用内存存储
   - 可以逐步迁移到使用Repository
   - 这样数据会持久化到数据库

## 🎯 数据持久化

**当前状态：**
- ✅ 数据库连接已配置
- ✅ 表结构已创建
- ✅ Repository接口已创建
- ⚠️ Service层仍使用内存存储（Map）

**要启用数据库存储，需要：**
1. 修改Service层，使用Repository代替Map
2. 实现Entity和Domain对象的转换
3. 处理复杂对象（如二维数组）的序列化

**注意：** 当前项目仍然可以使用内存存储。数据库连接主要用于验证配置是否正确，以及为未来的数据持久化做准备。

## 📚 相关文件

- `src/main/resources/application.properties` - 数据库配置
- `src/main/resources/db/schema.sql` - 数据库表结构
- `src/main/java/com/minesweeper/config/DatabaseConfig.java` - 连接测试
- `src/main/java/com/minesweeper/game/repository/` - Repository接口
- `src/main/java/com/minesweeper/game/model/Entity/` - JPA实体类

## 🎉 总结

数据库连接配置已完成！项目现在可以：

1. ✅ 连接到MySQL数据库
2. ✅ 自动创建表结构
3. ✅ 测试数据库连接
4. ✅ 为数据持久化做好准备

启动项目后，如果看到"数据库连接成功"的日志，说明配置正确！

