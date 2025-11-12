package com.minesweeper.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 数据库配置和连接测试
 * 应用启动时自动测试数据库连接
 */
@Component
@Order(1)
public class DatabaseConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        if (dataSource == null) {
            logger.warn("数据源未配置，跳过数据库连接测试");
            return;
        }

        try {
            // 测试数据库连接
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            
            logger.info("=========================================");
            logger.info("数据库连接成功！");
            logger.info("数据库URL: {}", metaData.getURL());
            logger.info("数据库驱动: {}", metaData.getDriverName());
            logger.info("数据库版本: {}", metaData.getDatabaseProductVersion());
            logger.info("=========================================");
            
            // 检查数据库是否存在
            if (jdbcTemplate != null) {
                try {
                    String databaseName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
                    logger.info("当前数据库: {}", databaseName);
                    
                    // 检查表是否存在
                    String checkTableSQL = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ?";
                    Integer tableCount = jdbcTemplate.queryForObject(checkTableSQL, Integer.class, databaseName);
                    logger.info("数据库表数量: {}", tableCount);
                } catch (Exception e) {
                    logger.warn("无法查询数据库信息: {}", e.getMessage());
                }
            }
            
            connection.close();
            logger.info("数据库连接测试完成");
        } catch (Exception e) {
            logger.error("=========================================");
            logger.error("数据库连接失败！");
            logger.error("错误信息: {}", e.getMessage());
            logger.error("=========================================");
            logger.error("请检查：");
            logger.error("1. MySQL服务是否启动");
            logger.error("2. 数据库用户名和密码是否正确");
            logger.error("3. 数据库是否已创建（minesweeper）");
            logger.error("4. application.properties中的数据库配置是否正确");
            logger.error("=========================================");
            // 不抛出异常，允许应用继续启动（如果数据库未配置）
            // throw e;
        }
    }
}

