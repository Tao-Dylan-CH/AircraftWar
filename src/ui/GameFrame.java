package ui;

import javax.swing.*;

/**
 * @author 挚爱之夕
 * @date 2022-01-29 - 01 - 29 - 22:06
 * @Description 游戏窗体
 * @Version 1.0
 */
@Deprecated
/**
 * 游戏界面
 * 后期加入里主界面，该类被弃用了
 */
public class GameFrame extends JFrame {
    public GameFrame(){
        this.setSize(615, 1040);
        this.setSize(600, 1000);
        this.setTitle("飞机大战");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setResizable(false);
        //窗口居中显示
        this.setLocationRelativeTo(null);
        //创建游戏面板
        GamePanel gamePanel = new GamePanel();
        //将面板添加到窗体
        this.add(gamePanel);
        this.setVisible(true);
        //监听键盘事件
//        this.addKeyListener(gamePanel);
    }
}
