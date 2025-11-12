# MySQL数据库设置指南

## 快速开始

### 1. 安装MySQL

确保您的系统已安装MySQL 8.0或更高版本。

### 2. 创建数据库

连接到MySQL，执行以下SQL创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS minesweeper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

或者直接运行项目提供的SQL脚本：

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. 配置数据库连接

编辑 `src/main/resources/application.properties` 文件，修改数据库连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/minesweeper?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=您的MySQL密码
```

**重要配置说明：**
- `localhost:3306` - MySQL服务器地址和端口（根据实际情况修改）
- `root` - MySQL用户名（根据实际情况修改）
- `您的MySQL密码` - MySQL密码（必须修改）
- `minesweeper` - 数据库名称

### 4. 启动项目

```bash
mvn spring-boot:run
```

### 5. 验证连接

启动后，查看控制台日志：

**成功连接时，会看到：**
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

**连接失败时，会看到错误信息和建议。**

## 数据库表结构

项目会自动创建以下表：

1. **user** - 用户表
   - 存储用户基本信息、游戏统计

2. **room** - 房间表
   - 存储房间信息

3. **room_player** - 房间玩家关联表
   - 存储房间中的玩家列表

4. **game** - 游戏表
   - 存储游戏状态、棋盘数据

5. **game_player** - 游戏玩家关联表
   - 存储游戏中的玩家列表

6. **user_token** - 用户Token表
   - 存储用户认证Token

## 手动创建表（可选）

如果您想手动创建表，可以运行SQL脚本：

```bash
mysql -u root -p minesweeper < src/main/resources/db/schema.sql
```

## 故障排除

### 问题1: 连接被拒绝

**错误信息：** `Communications link failure` 或 `Connection refused`

**解决方法：**
1. 检查MySQL服务是否启动
2. 检查MySQL端口是否为3306
3. 检查防火墙设置

### 问题2: 访问被拒绝

**错误信息：** `Access denied for user 'root'@'localhost'`

**解决方法：**
1. 检查用户名和密码是否正确
2. 检查用户是否有访问数据库的权限
3. 尝试重置MySQL密码

### 问题3: 数据库不存在

**错误信息：** `Unknown database 'minesweeper'`

**解决方法：**
1. 创建数据库：`CREATE DATABASE minesweeper;`
2. 或者修改application.properties中的数据库名称

### 问题4: 时区错误

**错误信息：** `The server time zone value 'XXX' is unrecognized`

**解决方法：**
1. 在连接URL中添加时区参数：`serverTimezone=Asia/Shanghai`
2. 或者在MySQL中设置时区：`SET time_zone = '+08:00';`

### 问题5: SSL连接错误

**错误信息：** `SSL connection error`

**解决方法：**
在连接URL中添加：`useSSL=false&allowPublicKeyRetrieval=true`

## 生产环境配置

### 1. 修改JPA配置

```properties
# 生产环境推荐使用validate，不自动修改表结构
spring.jpa.hibernate.ddl-auto=validate

# 关闭SQL日志
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN
```

### 2. 配置连接池

```properties
# HikariCP连接池配置
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 3. 使用环境变量

在生产环境中，建议使用环境变量配置数据库连接：

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/minesweeper}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
```

## 数据库备份和恢复

### 备份数据库

```bash
mysqldump -u root -p minesweeper > minesweeper_backup.sql
```

### 恢复数据库

```bash
mysql -u root -p minesweeper < minesweeper_backup.sql
```

## 下一步

数据库连接成功后，您可以：

1. **测试API** - 使用Postman或前端测试API接口
2. **查看数据** - 使用MySQL客户端查看数据库中的数据
3. **监控性能** - 查看日志中的SQL执行情况
4. **优化查询** - 根据实际使用情况优化数据库查询

## 技术支持

如果遇到问题，请检查：
1. MySQL版本是否兼容（推荐8.0+）
2. 数据库字符集是否为utf8mb4
3. 用户权限是否足够
4. 网络连接是否正常

