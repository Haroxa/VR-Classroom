package com.university.vrclassroombackend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 集成测试基类
 * <p>
 * 所有集成测试类应继承此类，提供以下功能：
 * 1. 自动加载测试环境配置（test profile）
 * 2. 每个测试方法后自动回滚数据库事务
 * 3. 提供数据库清理工具方法
 * </p>
 *
 * @author VR Classroom Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    @Autowired
    protected DataSource dataSource;

    /**
     * 每个测试方法执行前的准备工作
     * <p>
     * 子类可以重写此方法添加自定义的初始化逻辑
     * </p>
     */
    @BeforeEach
    void setUp() {
        // 子类可以重写此方法
    }

    /**
     * 清理指定表的数据
     * <p>
     * 用于测试前的数据清理，确保测试环境干净
     * </p>
     *
     * @param tableNames 要清理的表名列表
     * @throws SQLException 数据库操作异常
     */
    protected void cleanTables(String... tableNames) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // 关闭外键检查，避免删除顺序问题
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");

            for (String tableName : tableNames) {
                statement.execute("TRUNCATE TABLE " + tableName);
            }

            // 恢复外键检查
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * 执行SQL脚本
     * <p>
     * 用于插入测试数据
     * </p>
     *
     * @param sql SQL语句
     * @throws SQLException 数据库操作异常
     */
    protected void executeSql(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
