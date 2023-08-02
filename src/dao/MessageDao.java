package dao;

import ui.GamePanel;
import utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author 挚爱之夕
 * @date 2022-02-02 - 02 - 02 - 22:12
 * @Description 该类用于显示消息，控制音乐播放
 * @Version 1.0
 */
public class MessageDao {
    private static PlayerRecordDao playerRecordDao = null;
    //背景音乐
    public static boolean isSelectPlayBackgroundMusic = true;
    //爆炸音效
    public static boolean isSelectPlayBombSoundEffect  = true;
    //点击音效
    public static boolean isSelectPlayClickSoundEffect  = true;
    /*游戏帮助*/
    public static String getGameHelpContent() {//游戏帮助内容
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();

        String s = "";
        InputStream in = MessageDao.class.getResourceAsStream("/gameHelp.txt");
        assert in != null;
        //转换流指定字符集，不会出现中文乱码问题
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        try {
            reader = new BufferedReader(inputStreamReader);
            while((s = reader.readLine()) != null){
                result.append(s).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
    public static void showGameHelpMessage(JFrame frame){//在主菜单显示游戏帮助
        String message = MessageDao.getGameHelpContent();
        ImageIcon helpIcon = ImageUtil.getIcon("help.png");
//        JOptionPane.showMessageDialog(frame, message, "游戏帮助", JOptionPane.INFORMATION_MESSAGE, helpIcon);
        JOptionPane.showMessageDialog(frame, message, "游戏帮助", JOptionPane.INFORMATION_MESSAGE, helpIcon);
    }
    /*游戏设置*/
    public static void showGameSetting(JFrame parent){
        //显示对话框
        JDialog dialog = new JDialog(parent, "游戏设置");
        dialog.setLocationRelativeTo(parent);
        dialog.setBounds(parent.getBounds().x, parent.getBounds().y + 350, 600, 300);
        dialog.setIconImage(ImageUtil.getImage("setting.png"));
        dialog.setResizable(false);
        dialog.setVisible(true);
        //音乐标签
        JLabel musicLabel = new JLabel("音效设置");
        musicLabel.setBounds(10, -20, 600, 100);
        musicLabel.setFont(new Font("楷体", Font.BOLD, 20));
        dialog.add(musicLabel);
        //复选按钮：背景音乐、爆炸音效、按键音效
        JCheckBox checkBox1 = new JCheckBox("背景音乐");
        JCheckBox checkBox2 = new JCheckBox("爆炸音效");
        JCheckBox checkBox3 = new JCheckBox("按键音效");
        checkBox1.setBounds(10, 70, 100, 30);
        checkBox2.setBounds(200, 70, 100, 30);
        checkBox3.setBounds(10, 110, 100, 30);
        checkBox1.setFont(new Font("楷体", Font.BOLD, 15));
        checkBox2.setFont(new Font("楷体", Font.BOLD, 15));
        checkBox3.setFont(new Font("楷体", Font.BOLD, 15));
        //添加到对话框
        dialog.add(checkBox1);
        dialog.add(checkBox2);
        dialog.add(checkBox3);
        //事件监听
        if(isSelectPlayBackgroundMusic)
            checkBox1.setSelected(true);
        if(isSelectPlayBombSoundEffect)
            checkBox2.setSelected(true);
        if(isSelectPlayClickSoundEffect)
            checkBox3.setSelected(true);
        //背景音乐
        checkBox1.addItemListener(e -> {
            isSelectPlayBackgroundMusic = !isSelectPlayBackgroundMusic;
            if(isSelectPlayBackgroundMusic){
                AudioDao.backgroundMusic.start();
            }else{
                AudioDao.backgroundMusic.stop();
            }
        });
        //爆炸音效
        checkBox2.addItemListener(e -> {
            isSelectPlayBombSoundEffect = !isSelectPlayBombSoundEffect;
        });
        //点击音效
        checkBox3.addItemListener(e -> isSelectPlayClickSoundEffect = !isSelectPlayClickSoundEffect);
    }
    /*游戏暂停界面*/

    /**
     *
     * @param parent 游戏窗体
     * @return 0:继续游戏   1:返回主界面     -1:用户关闭对话框
     */
    public static int showGameStopMessage(JFrame parent){
        String[] selects = new String[]{"继续游戏", "返回主界面"};
        ImageIcon imageIcon = ImageUtil.getIcon("gameStopIcon.png");
        int choice = JOptionPane.showOptionDialog(parent, "吃口蘑菇,休息会儿...", "游戏暂停",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                imageIcon, selects, selects[0]);
        return choice;
    }

    /**
     * 显示玩家记录信息
     * @param parent 父窗口
     */
    public static void showTopPlayerRecorder(JFrame parent){
        //查询数据库
        playerRecordDao = new PlayerRecordDao();
        String sql = "select * from playerRecordTable ORDER BY time DESC";
        List<PlayerRecord> playerRecords = playerRecordDao.queryMultiply(sql, null);
        StringBuilder content = new StringBuilder("玩家\t分数\t游戏时长\t时间\t\n");
        for (PlayerRecord playerRecord:playerRecords
             ) {
            content.append(playerRecord.toString()).append("\n");
        }
        //对话框
        JDialog dialog = new JDialog(parent, "top 玩家");
        dialog.setIconImage(ImageUtil.getImage("topIcon.png"));
        dialog.setModal(true);
        dialog.setBounds(parent.getBounds().x - 100, parent.getBounds().y + 250, 800, 500);
        dialog.setResizable(false);
        //文本区
        JTextArea jTextArea = new JTextArea(content.toString());
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font("楷体", Font.BOLD, 30));
        dialog.add(new JScrollPane(jTextArea));
        //显示对话框
        dialog.setVisible(true);
    }

    public static int showChoiceRecordName(JFrame parent){
        String[] selects = new String[]{"好的", "不，我不要"};
        ImageIcon imageIcon = ImageUtil.getIcon("gameStopIcon.png");
        int choice = JOptionPane.showOptionDialog(parent, "恭喜通关，你可以选择上传记录到数据库", "选择记录",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                imageIcon, selects, selects[0]);
        return choice;
    }

    public static void showInputNameMessage(JFrame parent, GamePanel gamePanel){
        int choice = showChoiceRecordName(parent);
        if(choice == 0){//选择记录
            //显示对话框
            JDialog dialog = new JDialog(parent, "巅峰玩家登记");
            dialog.setBounds(parent.getBounds().x, parent.getBounds().y + 350, 600, 300);
            dialog.setVisible(true);
            dialog.setModal(true);
            dialog.setResizable(false);
            //标签
            JLabel jLabel = new JLabel("输入玩家名称：");
            jLabel.setBounds(10, -20, 150, 100);
            jLabel.setFont(new Font("楷体", Font.BOLD, 20));
            dialog.add(jLabel);
            //文本框
            JTextField jTextField = new JTextField();
            jTextField.setBounds(150, 15, 300, 30);
            jTextField.setFont(new Font("楷体", Font.BOLD, 20));
            dialog.add(jTextField);
            //按钮
            JButton button1 = new JButton("确认");
            button1.setBounds(300, 200, 100, 50);
            button1.setFont(new Font("楷体", Font.BOLD, 20));
            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int rows = 0;
                    try {
                        rows = Recorder.writeToDataBase(jTextField.getText(),
                                1314, gamePanel.getClock().getGameTime());
                    } catch (Exception ex) {
                        System.out.println("写入记录时发生异常");
                    }
                    dialog.dispose();
                    if(rows >= 1){
                        JOptionPane.showMessageDialog(parent, "你在数据库中留下名字：" + jTextField.getText());
                    }else{
                        JOptionPane.showMessageDialog(parent, "连接服务器失败！");
                    }
                }
            });
            JButton button2 = new JButton("取消");
            button2.setBounds(450, 200, 100, 50);
            button2.setFont(new Font("楷体", Font.BOLD, 20));
            button2.addActionListener(e -> dialog.dispose());
            dialog.add(button1);
            dialog.add(button2);
            dialog.repaint();
        }
    }
}
