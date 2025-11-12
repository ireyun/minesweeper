# 数据库配置说明

## 数据库设置

### 1. 创建数据库

首先需要在MySQL中创建数据库。可以使用以下SQL脚本：

```sql
CREATE DATABASE IF NOT EXISTS minesweeper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

或者直接运行 `src/main/resources/db/schema.sql` 文件。

### 2. 配置数据库连接

编辑 `src/main/resources/application.properties` 文件，修改数据库连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/minesweeper?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

**重要：** 请根据您的MySQL配置修改以下参数：
- `localhost:3306` - MySQL服务器地址和端口
- `root` - MySQL用户名
- `root` - MySQL密码

### 3. 数据库表结构

项目使用JPA自动创建表结构（`spring.jpa.hibernate.ddl-auto=update`），首次运行时会自动创建所有表。

表结构包括：
- `user` - 用户表
- `room` - 房间表
- `room_player` - 房间玩家关联表
- `game` - 游戏表
- `game_player` - 游戏玩家关联表
- `user_token` - 用户Token表

### 4. 手动创建表（可选）

如果需要手动创建表，可以运行 `src/main/resources/db/schema.sql` 文件。

## 测试数据库连接

### 1. 启动项目

```bash
mvn spring-boot:run
```

### 2. 检查日志

启动后，查看控制台日志，应该看到：
- 数据库连接成功的日志
- JPA创建表的SQL语句
- 如果没有错误，说明数据库连接成功

### 3. 验证表结构

连接到MySQL数据库，执行：

```sql
USE minesweeper;
SHOW TABLES;
```

应该能看到所有表都已创建。

## 故障排除

### 1. 连接失败

如果遇到连接失败错误，请检查：
- MySQL服务是否启动
- 数据库用户名和密码是否正确
- 数据库地址和端口是否正确
- 数据库是否已创建

### 2. 时区问题

如果遇到时区错误，请确保：
- MySQL时区设置正确
- application.properties中的时区设置正确（Asia/Shanghai）

### 3. 字符编码问题

确保数据库使用utf8mb4字符集：
```sql
ALTER DATABASE minesweeper CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. SSL连接问题

如果遇到SSL连接问题，可以在连接URL中添加：
```
useSSL=false&allowPublicKeyRetrieval=true
```

## 数据迁移

### 从内存存储迁移到数据库

当前项目支持两种存储方式：
1. 内存存储（ConcurrentHashMap）- 默认方式
2. 数据库存储（MySQL + JPA）- 需要配置数据库

要切换到数据库存储，需要：
1. 配置数据库连接
2. 修改Service层使用Repository而不是Map
3. 重启应用，数据会自动迁移到数据库

## 性能优化

### 1. 连接池配置

可以在application.properties中配置连接池：

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### 2. 索引优化

所有表都已创建了必要的索引，包括：
- 用户表的username和email索引
- 房间表的host_id和status索引
- 游戏表的room_id和game_status索引

### 3. 查询优化

使用JPA的懒加载和分页功能可以优化查询性能。

## 备份和恢复

### 备份数据库

```bash
mysqldump -u root -p minesweeper > minesweeper_backup.sql
```

### 恢复数据库

```bash
mysql -u root -p minesweeper < minesweeper_backup.sql
```

