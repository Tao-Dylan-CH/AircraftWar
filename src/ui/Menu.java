package ui;

import dao.AudioDao;
import dao.MessageDao;
import dao.Recorder;
import domain.*;
import utils.ImageUtil;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author 挚爱之夕
 * @date 2022-02-01 - 02 - 01 - 21:06
 * @Description 游戏主界面
 * @Version 1.0
 */
public class Menu extends JFrame implements Runnable{
    //背景
    private JLabel back;
    //主界面中的三个飞机
    private MenuPlane plane;
    private MenuPlane planeL;
    private MenuPlane planeR;
    //用于选择的五个小飞机
    private JLabel[] labels;
    private boolean startGame = false;
    public static Menu menu;

    public Menu(){
        //播放背景音乐
        AudioDao.playBackgroundMusic();
        this.setTitle("飞机大战  by  挚夕");
        this.setLayout(null);
        this.setSize(600, 1000);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(ImageUtil.getImage("MenuIcon.png"));
        //初始化
        init();
        //窗口可见
        this.setVisible(true);
        //事件监听
        setListener();
        //启动窗口线程
        new Thread(this).start();
        gameHelp();
        menu = this;
    }
    public void setListener(){
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(startGame){//没有开始游戏才监听
                    return;
                }
                AudioDao.playClickSound();
                int key = e.getKeyCode();
                if(key == KeyEvent.VK_ENTER){
                    int index = getVisibleLabelIndex();
                    switch (index){
                        case 0:
                            startGame();
                            break;
                        case 1:
                            continueLastGame();
                            break;
                        case 2:
                            gameHelp();
                            break;
                        case 3:
                            gameSetting();
                            break;
                        case 4:
                            topPlayers();
                            break;
                    }
                }
                //将当前小飞机设为不可视，上一个或最后一个设为可视，表现为选择效果
                if(key == KeyEvent.VK_UP){
                    int index = getVisibleLabelIndex();
                    labels[index].setVisible(false);
                    if(index == 0){
                        labels[labels.length - 1].setVisible(true);
                    }else{
                        labels[index - 1].setVisible(true);
                    }
                }
                //同理
                if(key == KeyEvent.VK_DOWN){
                    int index = getVisibleLabelIndex();
                    labels[index].setVisible(false);
                    if(index == labels.length - 1){
                        labels[0].setVisible(true);
                    }else{
                        labels[index + 1].setVisible(true);
                    }
                }
            }
        });
    }
    /**
     * 加载图片，将标签加入窗体
     */
    public void init(){
        //背景标签
        back = new JLabel(ImageUtil.getIcon("MainBackground.jpg"));
        //设置背景图
        setBackground();
        //五个用于用户选择的小飞机
        ImageIcon imageIcon = ImageUtil.getIcon("point.png");
        labels = new JLabel[5];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel(imageIcon);
            if(i == labels.length - 1){
                labels[i].setBounds(170, 630 + 70 * i - 15, imageIcon.getIconWidth(), imageIcon.getIconHeight());
            }else{
                labels[i].setBounds(170, 630 + 70 * i,imageIcon.getIconWidth(), imageIcon.getIconHeight());
            }
            this.add(labels[i]);
            //设置第一个可视，其他不可视
            labels[i].setVisible(i == 0);
        }

        plane = new MenuPlane(1);
        this.add(plane);
        planeL = new MenuPlane();
        this.add(planeL);
        planeR = new MenuPlane(2);
        this.add(planeR);
    }
    public static Menu getMenu(){
        if(menu == null){
            menu = new Menu();
        }
        return menu;
    }

    private void setBackground() {
        //设置背景图片位置大小
        back.setBounds(0, 0, 600, 1000);
        //面板透明
        JPanel j = (JPanel)getContentPane();
        j.setOpaque(false);
        //设置背景
        getLayeredPane().add(back, new Integer(Integer.MIN_VALUE));//背景添加到分层面板
    }

    /**
     * 得到标签组里，唯一一个可视的标签
     * 用户选择时用到
     * @return index
     */
    private int getVisibleLabelIndex(){
        for (int i = 0; i < labels.length; i++) {
            if(labels[i].isVisible()){
                return i;
            }
        }
        return -1;
    }
    //开始游戏
    private void startGame(){
        removeLabels();
        //创建一个持有该窗体对象的面板
        Recorder.gameIsStop = false;
        GamePanel gamePanel = new GamePanel(this);
        gamePanel.setBounds(0, 0, getWidth(), getHeight());
        add(gamePanel);
        gamePanel.requestFocusInWindow();//获得输入焦点
    }
    //继续上局游戏
    public void continueLastGame(){
        //得到保存的面板
        GamePanel gamePanel = Recorder.readGameRecord();
        if(gamePanel == null){//没有存档
            JOptionPane.showMessageDialog(this, "没有存档，请开始游戏");
            return;
        }
        //启动面板线程
        gamePanel.gamePanelRun = true;
        new Thread(gamePanel).start();
//        System.out.println("继续上局游戏");
        //继续游戏
        Recorder.gameIsStop = false;
        removeLabels();
        //设置游戏面板位置和大小
        gamePanel.setBounds(0, 0, getWidth(), getHeight());
        //添加到窗体
        add(gamePanel);
        gamePanel.requestFocusInWindow();////获得输入焦点

        /*重新启动线程*/
        //启动我方飞机
        startRun(gamePanel.getHero());
        //启动我方飞机子弹
//        for (Bullet bullet:gamePanel.getHero().getBullets()
//             ) {
//            startRun(bullet);
//        }
        // 增强遍历，返回菜单，继续游戏玩家处于攻速增加状态下，
        // 可能抛出 Concurrent Modification Exception，线程问题
        // 这里改用普通for循环
        for (int i = 0; i < gamePanel.getHero().getBullets().size(); i++) {
            Bullet bullet = gamePanel.getHero().getBullets().get(i);
            startRun(bullet);
        }
        //启动敌方飞机
        for (Enemy enemy:gamePanel.getEnemies()
             ) {
            startRun(enemy);
        }
        //启动敌方子弹
        for(Bullet bullet :gamePanel.getAllEnemiesBullets()){
            startRun(bullet);
        }
        //启动增益线程
        for (Food food:gamePanel.foods
             ) {
            startRun(food);
        }
        //继续显示爆炸效果
        for (Bomb bomb: gamePanel.bombs
             ) {
            startRun(bomb);
        }
        //重启计时器
        startRun(gamePanel.getClock());
    }

    /**
     * 用于重新启动游戏面板中飞机、子弹等的线程
     * @param thread 面板中飞机、子弹等
     */
    private void startRun(Runnable thread){
        new Thread(thread).start();
    }

    /**
     * 移除窗体中的标签
     */
    private void removeLabels() {
        startGame = true;//开始游戏
        Recorder.returnToMenu = false;
        //移除当前窗体的五个小飞机标签和3个飞机
        remove(back);
        remove(plane);
        remove(planeL);
        remove(planeR);
        for (JLabel label : labels) {
            remove(label);
        }
        //结束三个飞机的线程，作废
        plane.setLive(false);
        planeL.setLive(false);
        planeR.setLive(false);
    }

    //游戏帮助
    public void gameHelp(){
//        System.out.println("游戏帮助");
        MessageDao.showGameHelpMessage(this);
    }
    //游戏设置
    public void gameSetting(){
//        System.out.println("游戏设置");
        MessageDao.showGameSetting(this);
    }
    //巅峰玩家
    public void topPlayers(){
//        System.out.println("巅峰玩家");
        MessageDao.showTopPlayerRecorder(this);
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(startGame){
                continue;
            }
            repaint();
        }
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }

    public static void setMenu(Menu menu) {
        Menu.menu = menu;
    }
}
