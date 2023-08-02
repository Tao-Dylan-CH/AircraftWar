package domain;

import dao.Recorder;
import utils.ImageUtil;

import javax.swing.*;
import java.util.Random;

/**
 * @author 挚爱之夕
 * @date 2022-02-02 - 02 - 02 - 14:56
 * @Description 该类用于主菜单显示飞机飞行效果
 * @Version 1.0
 */
public class MenuPlane extends JLabel implements Runnable {
    private int x;
    private int y;
    private Bullet bullet;
    private boolean isLive = true;
    private int speed = 3;
    private int type;
    public MenuPlane(int flag) {//向右的飞机
        this.type = 1;
        ImageIcon image;
        image = getRandomRightImageIcon();
        super.setIcon(image);
        if(flag == 1){
            x = 0;
            y = 560;
        }else{
            x = 250;
            y = 250;
        }
//        this.setBounds(x, y, image.getIconWidth(), image.getIconHeight());
        this.setBounds(x, y, 86, 86);
        new Thread(this).start();
    }

    public MenuPlane() {//向左的飞机
        this.type = 2;
        ImageIcon image;
        image = getRandomLeftImageIcon();
        super.setIcon(image);
        x = 600;
        y = 120;
        this.setBounds(x, y, 64, 64);
        new Thread(this).start();
    }

    /**
     * 为向右的飞机随机生成图片
     * @return 图片
     */
    public ImageIcon getRandomRightImageIcon() {
        Random random = new Random();
        int index = random.nextInt(3) + 1;//1 or 2 or 3
        String imageName = "MenuPlane" + index + ".png";
        return ImageUtil.getIcon(imageName);
    }
    public ImageIcon getRandomLeftImageIcon(){
        Random random = new Random();
        int index = random.nextInt(3) + 4;//4 or 5 or 6
        String imageName = "MenuPlane" + index + ".png";
        return ImageUtil.getIcon(imageName);
    }
    //    public void shoot(){
//        bullet = new Bullet(x + image.getWidth(), y + 80, Direction.RIGHT);
//        bullet.setImage(ImageUtil.getImage("bullet_red.png"));
//        new Thread(bullet).start();
//    }
    @Override
    public void run() {
        while (isLive) {
            if (type == 1) {//这种类型的有3种,跑完随机换图
                x += speed;
                if (x > 600) {
                    super.setIcon(getRandomRightImageIcon());
                    x = 0;
                }
            } else {//这中类型有3种,跑完随机换图
                x -= speed;
                if (x + 64 < 0) {
                    super.setIcon(getRandomLeftImageIcon());
                    x = 600;
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public Bullet getBullet() {
        return bullet;
    }

    public void setBullet(Bullet bullet) {
        this.bullet = bullet;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
