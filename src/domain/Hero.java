package domain;

import dao.Recorder;
import sun.net.www.content.image.png;
import utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/**
 * @author 挚爱之夕
 * @date 2022-01-30 - 01 - 30 - 15:14
 * @Description 我方战机
 * @Version 1.0
 */
public class Hero extends FlyObject implements Runnable{
    private List<Bullet> bullets;//子弹集
    private int shootInterval;//发射子弹间隔
    private int bleed;//血量
    private BufferedImage bulletImage;
    private int index = 1;
    private boolean isWinner = false;
    public Hero() {
        this.x = 230;
        this.y = 830;
        speed = 15;
        imageName = "plane"+ index +".png";
        image = ImageUtil.getImage(imageName);
        bullets = new ArrayList<>();
        shootInterval = 1000;
        bleed = 100;
        bulletImage = ImageUtil.getImage("bullet_purple.png");
    }
    public void shoot(){
        Bullet bullet = new Bullet();
        //x + image.getWidth() / 2 - 20, y - 50, Direction.UP
        bullet.setImage(bulletImage);
        bullet.setX(x + image.getWidth() /2 - bullet.image.getWidth() / 2);
        bullet.setY(y - 20);
        bullet.setDirection(Direction.UP);
        bullet.setSpeed(10);
        new Thread(bullet).start();
        bullets.add(bullet);
    }
    //键盘方式控制
    public void moveUp(){
        y -= speed;
    }
    public void moveDown(){
        y += speed;
    }
    public void moveRight(){
        x += speed;
    }
    public void moveLeft(){
        x -= speed;
    }
    //鼠标方式控制
    public void moveToMouse(int mx, int my){
        this.x = mx;
        this.y = my;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }
    public void moveToVictory(){
        y -= speed;
    }
    @Override
    public void run() {
        while(isLive){
            if(Recorder.returnToMenu){
                break;
            }
            if(Recorder.gameIsStop || isWinner){//赢了或游戏暂停
                continue;//不再发射子弹和改变外观
            }
            shoot();
            //显示玛丽奥动作
            if(index != 4){
                index++;
            }else{
                index = 1;
            }
            image = ImageUtil.getImage("plane"+ index +".png");
            try {
                Thread.sleep(shootInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getShootInterval() {
        return shootInterval;
    }

    public void setShootInterval(int shootInterval) {
        this.shootInterval = shootInterval;
    }

    public int getBleed() {
        return bleed;
    }

    public void setBleed(int bleed) {
        this.bleed = bleed;
    }

    public BufferedImage getBulletImage() {
        return bulletImage;
    }

    public void setBulletImage(BufferedImage bulletImage) {
        this.bulletImage = bulletImage;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
