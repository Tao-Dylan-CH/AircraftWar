package dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author 挚爱之夕
 * @date 2022-02-05 - 02 - 05 - 14:21
 * @Description 使用德鲁伊数据库连接池和阿帕奇工具查询数据库
 * @Version 1.0
 */
public class BasicDao<T> {
    private final QueryRunner queryRunner = new QueryRunner();
    //多行
    public List<T> queryMultiply(String sql, Class<T> clazz, Object... parameters){
        Connection connection = null;
        try {
            connection = DruidUtil.getConnection();
            return queryRunner.query(connection, sql, new BeanListHandler<>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally{
            DruidUtil.closeConnection(connection, null, null);
        }
    }
    //单行
    public T querySignal(String sql, Class<T> clazz, Object... parameters){
        Connection connection = null;
        try {
            connection = DruidUtil.getConnection();
            return queryRunner.query(connection, sql, new BeanHandler<>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            DruidUtil.closeConnection(connection, null, null);
        }
    }
    //单行单列
    public Object queryScalar(String sql, Class<T> clazz, Object... parameters){
        Connection connection = null;
        try {
            connection = DruidUtil.getConnection();
            return queryRunner.query(connection, sql, new ScalarHandler(), parameters);
        } catch (SQLException e) {
            throw new RuntimeException();
        }finally {
            DruidUtil.closeConnection(connection, null, null);
        }
    }
}
