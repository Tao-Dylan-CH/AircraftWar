package utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author 挚爱之夕
 * @date 2022-02-05 - 02 - 05 - 14:24
 * @Description 使用德鲁伊数据连接池获取mysql连接
 * @Version 1.0
 */
public class DruidUtil {
    private static DataSource dataSource = null;
    //加载
    static {
        Properties properties = new Properties();
        try {
            InputStream inputStream = DruidUtil.class.getResourceAsStream("/druid.properties");
            properties.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException("使用德鲁伊异常");
        }
    }

    /**
     *  得到连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 关闭连接
     */
    public static void closeConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            if(connection != null){
                connection.close();
            }
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
