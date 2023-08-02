package domain;


import dao.Recorder;
import utils.ImageUtil;
import ui.GamePanel;

import java.io.Serializable;
import java.util.Random;

/**
 * @author 挚爱之夕
 * @date 2022-01-30 - 01 - 30 - 16:50
 * @Description 敌方战机
 * @Version 1.0
 */
public class Enemy extends Bullet implements Runnable{
    private Bullet bullet;
    private GamePanel gamePanel;
    public Enemy(){
        Random random = new Random();
        //随机生成1-5的整数
        int index = random.nextInt(5) + 1;
        //随机敌机外观
        this.image = ImageUtil.getImage("enemy0" + index + ".png");
        //随机坐标
        this.x = random.nextInt(600 - image.getWidth());
        this.y = 0;
        //启动线程
        new Thread(this).start();
    }
    public void shoot(){
        bullet = new Bullet(x + image.getWidth() / 2 - 15, y + 80, Direction.DOWN);
        bullet.setSpeed(10);
        bullet.setImage(ImageUtil.getImage("bullet_enemy.png"));
        new Thread(bullet).start();
        gamePanel.getAllEnemiesBullets().add(bullet);//将子弹添加到面板定义的字段
    }
    @Override
    public void run() {
        while(isLive){
            if(Recorder.returnToMenu){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Recorder.gameIsStop){//游戏暂停什么也不做
                continue;
            }
            //向下移动
            this.y += speed;
            if(y > 1000)//越界
                isLive = false;
            //同时间只发射一颗子弹
            if(bullet == null || !bullet.isLive){
                shoot();
            }


        }
    }

    public Bullet getBullet() {
        return bullet;
    }

    public void setBullet(Bullet bullet) {
        this.bullet = bullet;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}
