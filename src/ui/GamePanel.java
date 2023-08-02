package ui;


import dao.AudioDao;
import dao.MessageDao;
import dao.Recorder;
import utils.ImageUtil;
import domain.*;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 挚爱之夕
 * @date 2022-01-29 - 01 - 29 - 22:13
 * @Description 游戏面板
 * @Version 1.0
 */
@SuppressWarnings({"all"})
public class GamePanel extends JPanel implements Runnable {
    //背景
    private BufferedImage bg = null;
    //我方战机
    private Hero hero = null;
    //敌方战机
    private List<Enemy> enemies = null;
    //敌方战机的子弹
    private List<Bullet> allEnemiesBullets;
    //得分
    private int score = 0;
    //显示爆炸效果
    List<Bomb> bombs;
    //我方失败，爆炸
    Bomb heroExplode;
    //增益效果
    List<Food> foods;
    //一次随机生成的最大战机数
    private int maxCount = 3;
    //这种方式，操作飞机移动更流畅
    boolean pressUp = false, pressRight = false, pressDown = false, pressLeft = false;
    //游戏没有结束的判定
    boolean gameRunning = true;
    //记录游戏进行时间
    Clock clock;
    //记录主界面
    Menu menu;
    //控制面板run线程
    boolean gamePanelRun = true;
    //本类对象
    GamePanel gamePanel;

    public GamePanel(Menu menu) {
        gamePanel = this;
        this.menu = menu;
        //背景
        this.setBackground(Color.PINK);
        bg = ImageUtil.getImage("bg01.png");
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {//监听鼠标移动
                if (Recorder.gameIsStop || !gameRunning) {//游戏暂停或结束，不再响应
                    return;
                }
                int mx = e.getX() - 55;
                int my = e.getY() - 40;
                int x;
                int y;
                int width = hero.getImage().getWidth();
                int height = hero.getImage().getHeight();
                //防止出边界
                if (mx + width > 600) {
                    x = 600 - width;
                } else if (mx < 0) {
                    x = 0;
                } else {    //没有出界
                    x = mx;
                }

                if (my < 0) {
                    y = 0;
                } else if (my + height > 1000) {
                    y = 1000 - height;
                } else {
                    y = my;
                }
//                hero.moveToMouse(mx - 55, my - 40);
                hero.moveToMouse(x, y);
            }
        };
        //监听鼠标事件
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        //监听键盘事件
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameRunning) {//游戏结束，不再监听键盘事件
                    return;
                }
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (Recorder.returnToMenu)
                        return;
                    Recorder.gameIsStop = !Recorder.gameIsStop;
                    if (Recorder.gameIsStop) {////游戏暂停，弹出对话框
                        int choice = MessageDao.showGameStopMessage(menu);
                        if (choice == 1) {//返回主界面
                            returnToMenu();
                        } else {//继续游戏
                            Recorder.gameIsStop = false;
                        }
                    }
                }
                if (Recorder.gameIsStop) {//游戏暂停不响应移动
                    return;
                }
                if (key == KeyEvent.VK_UP) {
                    pressUp = true;
                }
                if (key == KeyEvent.VK_RIGHT) {
                    pressRight = true;
                }
                if (key == KeyEvent.VK_DOWN) {
                    pressDown = true;
                }
                if (key == KeyEvent.VK_LEFT) {
                    pressLeft = true;
                }
//        if(key == KeyEvent.VK_J){
//            System.out.println("jjj");
//            hero.shoot();
//        }
//        System.out.println("hero: " + hero.getX() + " " + hero.getY());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (Recorder.gameIsStop || !gameRunning) {//游戏暂停或结束，不再监听键盘事件
                    return;
                }
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    pressUp = false;
                }
                if (key == KeyEvent.VK_RIGHT) {
                    pressRight = false;
                }
                if (key == KeyEvent.VK_DOWN) {
                    pressDown = false;
                }
                if (key == KeyEvent.VK_LEFT) {
                    pressLeft = false;
                }
            }
        });
//        this.requestFocusInWindow();//在窗体类中调用，否则无用
        //用于存放所有敌机的子弹
        allEnemiesBullets = new ArrayList<>();
        JOptionPane.showMessageDialog(this, "目标分数：1314 ");
        //初始化
        init();
        //开始
        action();
        new Thread(this).start();
        //记录游戏时间
        clock = new Clock();
    }

    /**
     * 回到主菜单
     */
    private void returnToMenu() {
        //保存游戏
        Recorder.keepGame(gamePanel, menu);
        //结束面板线程
        this.gamePanelRun = false;
        //返回主界面
        menu.setStartGame(false);
        menu.remove(gamePanel);//移除游戏面板
        menu.init();
        Recorder.returnToMenu = true; //废弃该面板，结束由其生成的所有线程
    }

    /**
     * 游戏成功或失败
     * 回到主菜单
     */
    private void backToMenu() {
        //清除记录
        Recorder.setGamePanel(null);
        //结束面板线程
        this.gamePanelRun = false;
        //返回主界面
        menu.setStartGame(false);
        menu.remove(gamePanel);//移除游戏面板
        menu.init();
        menu.requestFocusInWindow();//获得输入焦点，一定要带上，否者不响应事件
        Recorder.returnToMenu = true; //废弃该面板，结束由其生成的所有线程
    }

    public GamePanel(GamePanel gamePanel, Menu parent) {
        this(parent);
        this.hero = gamePanel.hero;
        this.enemies = gamePanel.enemies;
        this.allEnemiesBullets = gamePanel.allEnemiesBullets;
        this.score = gamePanel.score;
        this.bombs = gamePanel.bombs;
        this.heroExplode = gamePanel.heroExplode;
        this.foods = gamePanel.foods;
        this.maxCount = gamePanel.maxCount;
        this.gameRunning = gamePanel.gameRunning;
    }

    public static class Clock extends Thread {
        int start = 0;
        int end = 0;

        public Clock() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            while (true) {
                if (Recorder.returnToMenu) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Recorder.gameIsStop) {//游戏暂停不计时
                    continue;
                }
                end++;
            }
        }

        public int getGameTime() {
            return end - start;
        }
    }
//    public long getGameTime() {
//        long end = System.currentTimeMillis();
//        return end - start;
//    }

    public void adjustGameDifficultLevel() {
//        int gameTime = Clock.getGameTime();
//
//        maxCount = (int) i + 3;
        int gameTime = clock.getGameTime();
        maxCount = gameTime / 30 + 3;
    }

    /**
     * 该方法启动一个线程，不断生成敌机
     */
    public void action() {//不断生成敌机

        new Thread(() -> {
            while (gameRunning) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Recorder.gameIsStop) {//游戏暂停不生成
                    continue;
                }
                //生成敌机
                enemyEnter();
                //生成增益
                generateFoods();
                //根据游戏时间增加游戏难度
                adjustGameDifficultLevel();
            }
        }).start();
    }

    /**
     * 创建敌机
     */
    public void enemyEnter() {
        Random random = new Random();
        //一次生成1-maxCount架敌机
        int count = random.nextInt(maxCount) + 1;
//        System.out.println(count);
        for (int i = 0; i < count; i++) {
            Enemy enemy = new Enemy();
            //每个敌方战机，持有当前面板，方便在其死亡后绘制其子弹
            enemy.setGamePanel(this);
            enemies.add(enemy);
        }
    }

    /**
     * 爆破所有现存敌机
     * 用于游戏通关时
     */
    public void blastingAllEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.setLive(false);
            Bomb bomb = getBomb(enemy);
            new Thread(bomb).start();
            bombs.add(bomb);
        }
    }

    public void generateFoods() {
        Food food = null;
        Random random = new Random();
        int num = random.nextInt(100);//0-99
        if (num < 10) {             //10%生成红色子弹增益
            food = new Food(FoodType.RED_BULLET);
        } else if (num < 20) {         //10%生成紫色子弹增益
            food = new Food(FoodType.PURPLE_BULLET);
        } else if (num < 30) {       //10%生成生命增益
            food = new Food(FoodType.LIFE);
        } else if (num < 40) {        //10%生成攻速增益
            food = new Food(FoodType.IncreasedFiringSpeed);
        }
        if (food != null) {
            foods.add(food);
        }
    }

    private void init() {
        //创建我方战机，并自动发射子弹
        hero = new Hero();
        new Thread(hero).start();
        //创建敌方战机集
        enemies = new ArrayList<>();
        //创建炸弹集，显示爆炸效果
        bombs = new ArrayList<>();
        //播放背景音乐:加入主界面后，在Menu里播放了
//        AudioDao.playBackgroundMusic();
        //增益效果
        foods = new ArrayList<>();
    }

    /**
     * 判断子弹是否击中飞机
     *
     * @param bullet 子弹
     * @param plane  飞机
     * @return true if bullet hit plane or false if not
     */
    public boolean bulletHitPlane(Bullet bullet, FlyObject plane) {
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        int bulletWidth = bullet.getImage().getWidth();
        int bulletHeight = bullet.getImage().getHeight();
        int planeX = plane.getX();
        int planeY = plane.getY();
        int planeWidth = plane.getImage().getWidth();
        int planeHeight = plane.getImage().getHeight();
        if (bullet.getDirection() == Direction.UP) {//我方子弹
            if (bulletX + bulletWidth > planeX && bulletX < planeX + planeWidth
                    && bulletY > planeY && bulletY < planeY + planeHeight) {
                return true;
            }
        } else if (bullet.getDirection() == Direction.DOWN) {//敌方子弹
            if (bulletX + bulletWidth > planeX && bulletX < planeX + planeWidth
                    && bulletY + bulletHeight > planeY
                    && bulletY + bulletHeight < planeY + planeHeight) {
                return true;
            }
        }
        return false;
    }

    public void checkBulletHitPlane() {
        //我方子弹打敌方战机
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            List<Bullet> bullets = hero.getBullets();
            for (int j = 0; j < bullets.size(); j++) {
                Bullet bullet = bullets.get(j);
                if (bulletHitPlane(bullet, enemy)) {//击中敌方战机
                    score += 10;
                    enemy.setLive(false);
                    bullet.setLive(false);
                    //爆炸效果
                    Bomb bomb = getBomb(enemy);
                    new Thread(bomb).start();
                    bombs.add(bomb);
                    //爆炸声
                    AudioDao.playBreakSound();
                }
            }
        }
        //敌方子弹打我方战机
        for (int i = 0; i < allEnemiesBullets.size(); i++) {
            Bullet bullet = allEnemiesBullets.get(i);
            if (gameRunning && bulletHitPlane(bullet, hero)) {//我方被击中
                bullet.setLive(false);
                hero.setBleed(hero.getBleed() - 10);
            }
        }
    }

    /**
     * 判断我方战机碰到飞行物
     *
     * @param flyObject 飞行物:敌机、道具...
     * @return true or false
     */
    public boolean isTouch(Bullet flyObject) {
        int heroX = hero.getX();
        int heroY = hero.getY();
        int heroWidth = hero.getImage().getWidth();
        int heroHeight = hero.getImage().getHeight();
        int flyObjectX = flyObject.getX();
        int flyObjectY = flyObject.getY();
        int flyObjectWidth = flyObject.getImage().getWidth();
        int flyObjectHeight = flyObject.getImage().getHeight();
        //我方相对于物体在下方被撞
        if (heroX + heroWidth > flyObjectX && heroX < flyObjectX + flyObjectWidth
                && heroY < flyObjectY + flyObjectHeight && heroY > flyObjectY) {
            return true;
        }
        //我方相对于物体在上方被撞
        if (heroX + heroWidth > flyObjectX && heroX < flyObjectX + flyObjectWidth
                && heroY + heroHeight > flyObjectY
                && heroY + heroHeight < flyObjectY + flyObjectHeight) {
            return true;
        }
        return false;
    }

    /**
     * 我方获得增益
     */
    public void heroGetFood(Food food) {
        FoodType type = food.getType();
        switch (type) {
            case RED_BULLET:
                hero.setBulletImage(ImageUtil.getImage("bullet_red.png"));
                break;
            case PURPLE_BULLET:
                hero.setBulletImage(ImageUtil.getImage("bullet_purple.png"));
                break;
            case LIFE:
                int bleed = hero.getBleed();
                if (bleed < 100) {
                    hero.setBleed(bleed + 10);
                }
                break;
            case IncreasedFiringSpeed:
                hero.setShootInterval(200);
                new Thread() {//增加射速持续10s
                    private long start = System.currentTimeMillis();
                    private long end;

                    @Override
                    public void run() {
                        while (true) {
                            end = System.currentTimeMillis();
                            if (end - start > 10000) {
                                hero.setShootInterval(1000);
                                break;
                            }
                        }
                    }
                }.start();
                break;
        }
        food.setLive(false);
    }

    /**
     * 用于显示爆炸效果
     * 得到一个位于物体中间的炸弹
     *
     * @param object 物体：敌机、我方飞机...
     * @return 一个炸弹
     */
    private Bomb getBomb(FlyObject object) {
        return new Bomb(object.getX() + object.getImage().getWidth() / 2 - 20
                , object.getY() + object.getImage().getHeight() / 2);
    }

    public boolean noBombExist() {
        for (Bomb bomb : bombs
        ) {
            if (bomb.isLive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //背景
        g.drawImage(bg, 0, 0, 600, 1000, null);
        //画我方战机
        if (hero.isLive()) {
            g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
        } else {//我方死亡坠落
            g.drawImage(ImageUtil.getImage("plane5.png"), hero.getX() - 10, hero.getY() + 10, null);
            hero.setY(hero.getY() + 10);
        }
        //画敌方战机
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), null);
        }
        /*画子弹*/

        //我方子弹
        List<Bullet> bullets = hero.getBullets();
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet.isLive()) {
                g.drawImage(bullet.getImage(), bullet.getX(), bullet.getY(), null);
            }
        }
        //敌方子弹
//        for (int i = 0; i < enemies.size(); i++) {//这种敌机死亡，其子弹无法绘制
//            Enemy enemy = enemies.get(i);
//            Bullet bullet = enemy.getBullet();
//            if(bullet.isLive()){
//                g.drawImage(bullet.getImage(), bullet.getX(), bullet.getY(), null);
//            }
//        }
        //画敌方子弹
        for (int i = 0; i < allEnemiesBullets.size(); i++) {
            Bullet bullet = allEnemiesBullets.get(i);
            if (bullet.isLive()) {
                g.drawImage(bullet.getImage(), bullet.getX(), bullet.getY(), null);
            }
        }
        //画增益物
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            if (food.isLive()) {
                g.drawImage(food.getImage(), food.getX(), food.getY(), null);
            } else {
                foods.remove(food);
            }
        }
        /*爆炸效果*/
        //敌方
        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            if (bomb != null && bomb.isLive()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), null);
            }
        }
        //我方
        if (heroExplode != null && heroExplode.isLive()) {
            g.drawImage(heroExplode.getImage(), heroExplode.getX(), heroExplode.getY(), null);
        }

        //分数、我方血量、游戏时间
        g.setColor(Color.YELLOW);
        g.setFont(new Font("楷体", Font.BOLD, 30));
        int bleed = hero.getBleed() < 0 ? 0 : hero.getBleed();
        g.drawString("血量: " + bleed, 10, 30);
        g.drawString("分数：" + score, 200, 30);
        g.drawString("用时：" + clock.getGameTime() + "s", 400, 30);

        // g.drawImage(ImageUtil.getImage("1.jpeg"), 0, 0, 600, 1000,null);
//        g.drawImage(ImageUtil.getImage("icon.jpeg"), 0,0,null);
//        g.setColor(Color.blue);
//        g.setFont(new Font("楷体", Font.BOLD, 50));
//        g.drawString("飞机大战", 100, 100);
    }

    @Override
    public void run() {
        while (gamePanelRun) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Recorder.gameIsStop) {
                continue;
            }
            repaint();
            //键盘控制的移动
            heroMove();
            //清除死亡的敌方战机
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (!enemy.isLive()) {
                    enemies.remove(enemy);
                } else {//判断我方战机是否与敌机相撞
                    if (isTouch(enemy)) {//相撞
                        enemy.setLive(false);
                        //爆炸效果
                        Bomb bomb = getBomb(enemy);
                        new Thread(bomb).start();
                        bombs.add(bomb);
                        hero.setBleed(hero.getBleed() - 20);
                        //播放爆炸声
                        AudioDao.playBreakSound();
                    }
                }
            }
            /*清除无效的子弹*/
            //我方子弹
            List<Bullet> bullets = hero.getBullets();
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if (!bullet.isLive()) {
                    bullets.remove(bullet);
                }
            }
            //敌方子弹
            for (int i = 0; i < allEnemiesBullets.size(); i++) {
                Bullet bullet = allEnemiesBullets.get(i);
                if (!bullet.isLive()) {
                    allEnemiesBullets.remove(bullet);
                }
            }

            //判断子弹是否击中飞机
            checkBulletHitPlane();
            //判断我方是否死亡
            if (hero.isLive() && hero.getBleed() <= 0) {
                if (isPressUp()) {
                    pressUp = false;
                }
//                if(isPressRight()){//不带上如果我方飞机血量为0，玩家按着右键，玛丽会有一个右倾过程
//                    pressRight = false;
//                }
                if (isPressDown()) {
                    pressDown = false;
                }
//                if(isPressLeft()){//不带上如果我方飞机血量为0，玩家按着左键，玛丽会有一个左倾过程
//                    pressLeft = false;
//                }
                hero.setLive(false);
                Bomb bomb = getBomb(hero);
                new Thread(bomb).start();
                AudioDao.playHeroExplodeSound();
                heroExplode = bomb;
                gameRunning = false;
            }
            //我方飞机爆炸后，马里奥坠落出界面，提示游戏结束，退出
            if (heroExplode != null && !heroExplode.isLive() && hero.getY() > 1000) {
                gameRunning = false;
                JOptionPane.showMessageDialog(this, "游戏失败");
//                System.exit(0);
                backToMenu();
            }
            //游戏通关
            if (score >= 1314) {
                if (isPressDown()) {
                    pressDown = false;
                }
                blastingAllEnemy();
                hero.setWinner(true);
                hero.setSpeed(7);
                pressUp = true; //这时我方向上移动，移动出界面，游戏结束
                gameRunning = false;
                score = 1314;
                hero.setImage(ImageUtil.getImage("plane6.png"));
            }
            if (gameRunning == false && noBombExist() && hero.getY() + hero.getImage().getHeight() < 0) {
                JOptionPane.showMessageDialog(this, "游戏通关");
//                System.exit(0);
                backToMenu();
                MessageDao.showInputNameMessage(menu, this);//提示输入名称，上传数据库
            }
            //爆炸结束从炸弹集中移除炸弹
            for (int i = 0; i < bombs.size(); i++) {
                Bomb bomb = bombs.get(i);
                if (!bomb.isLive()) {
                    bombs.remove(bomb);
                }
            }
            //我方获得增益
            for (int i = 0; i < foods.size(); i++) {
                Food food = foods.get(i);
                if (isTouch(food)) {
                    heroGetFood(food);
                }
            }
        }
    }


    public void heroMove() {
        int l = hero.getSpeed();
        int w = hero.getImage().getWidth();
        int h = hero.getImage().getHeight();
        if (hero.isWinner() || (pressUp && hero.getY() - l >= 0)) {
            hero.moveUp();
        }
        if (pressRight && hero.getX() + w + l <= 600) {
            hero.moveRight();
        }
        if (pressDown && hero.getY() + h + l <= 1000) {
            hero.moveDown();
        }
        if (pressLeft && hero.getX() - l >= 0) {
            hero.moveLeft();
        }

    }

    public BufferedImage getBg() {
        return bg;
    }

    public void setBg(BufferedImage bg) {
        this.bg = bg;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public List<Bullet> getAllEnemiesBullets() {
        return allEnemiesBullets;
    }

    public void setAllEnemiesBullets(List<Bullet> allEnemiesBullets) {
        this.allEnemiesBullets = allEnemiesBullets;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void setBombs(List<Bomb> bombs) {
        this.bombs = bombs;
    }

    public Bomb getHeroExplode() {
        return heroExplode;
    }

    public void setHeroExplode(Bomb heroExplode) {
        this.heroExplode = heroExplode;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }


    public boolean isPressUp() {
        return pressUp;
    }

    public void setPressUp(boolean pressUp) {
        this.pressUp = pressUp;
    }

    public boolean isPressRight() {
        return pressRight;
    }

    public void setPressRight(boolean pressRight) {
        this.pressRight = pressRight;
    }

    public boolean isPressDown() {
        return pressDown;
    }

    public void setPressDown(boolean pressDown) {
        this.pressDown = pressDown;
    }

    public boolean isPressLeft() {
        return pressLeft;
    }

    public void setPressLeft(boolean pressLeft) {
        this.pressLeft = pressLeft;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public boolean isGamePanelRun() {
        return gamePanelRun;
    }

    public void setGamePanelRun(boolean gamePanelRun) {
        this.gamePanelRun = gamePanelRun;
    }

    @Deprecated
    public GamePanel() {
        //背景
        this.setBackground(Color.PINK);
        bg = ImageUtil.getImage("bg01.png");
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {//监听鼠标移动
                if (Recorder.gameIsStop || !gameRunning) {//玩家飞机爆炸，不再响应
                    return;
                }
                int mx = e.getX() - 55;
                int my = e.getY() - 40;
                int x;
                int y;
                int width = hero.getImage().getWidth();
                int height = hero.getImage().getHeight();
                //防止出边界
                if (mx + width > 600) {
                    x = 600 - width;
                } else if (mx < 0) {
                    x = 0;
                } else {    //没有出界
                    x = mx;
                }

                if (my < 0) {
                    y = 0;
                } else if (my + height > 1000) {
                    y = 1000 - height;
                } else {
                    y = my;
                }
//                hero.moveToMouse(mx - 55, my - 40);
                hero.moveToMouse(x, y);
            }
        };
        //监听鼠标事件
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);

        //用于存放所有敌机的子弹
        allEnemiesBullets = new ArrayList<>();
        JOptionPane.showMessageDialog(this, "目标分数：1314 ");
        //初始化
        init();
        //开始
        action();
        new Thread(this).start();
        //记录游戏时间
        clock = new Clock();
    }
}
