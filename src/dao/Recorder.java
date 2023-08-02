package dao;

import com.sun.corba.se.impl.orbutil.ObjectWriter;
import com.sun.org.apache.bcel.internal.generic.DREM;
import domain.MenuPlane;
import ui.GamePanel;
import ui.Menu;
import utils.DruidUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 挚爱之夕
 * @date 2022-02-03 - 02 - 03 - 17:47
 * @Description 该类用于游戏的保存
 * @Version 1.0
 */
public class Recorder {
    private static final String path = "src/AircraftWar.data";
    //通过控制线程run方法，控制游戏暂停
    public static boolean gameIsStop = false;
    //返回主菜单，结束当前游戏面板中的飞机、子弹等线程，废弃该面板
    public static boolean returnToMenu = false;
    //定义IO对象，写数据到文件
    private static BufferedReader br;
    private static BufferedWriter bw;
    //游戏面板
    private static GamePanel gamePanel = null;
    //游戏窗体
    private static Menu menu = null;
    //日期格式化
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void keepGame(GamePanel gamePanel, Menu menu){
        Recorder.menu = menu;
        Recorder.gamePanel = gamePanel;
//        try {
//            bw = new BufferedWriter(new FileWriter(path));
//            /*我方战机*/
//            bw.write("我方战机");
//            //保存到本地文件的功能...
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                bw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
    public static int writeToDataBase(String name, int score, int gameTime){
        String sql = "insert into playerRecordTable values(null,?,?,?,?)";
        try {
            Connection connection = DruidUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, 1314);
            preparedStatement.setInt(3, gameTime);
            preparedStatement.setString(4, dateTimeFormatter.format(LocalDateTime.now()));
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("获取连接失败或sql语句异常");
        }
    }
    public static GamePanel readGameRecord(){
        return gamePanel;
    }
    public static GamePanel getGamePanel() {
        return gamePanel;
    }

    public static void setGamePanel(GamePanel gamePanel) {
        Recorder.gamePanel = gamePanel;
    }
}
