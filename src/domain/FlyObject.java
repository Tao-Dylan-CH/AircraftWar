package domain;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author 挚爱之夕
 * @date 2022-01-30 - 01 - 30 - 16:55
 * @Description 战机
 * @Version 1.0
 */
public class FlyObject{
    protected int x;
    protected int y;
    protected int speed = 5;
    protected BufferedImage image = null;
    protected String imageName;
    protected boolean isLive = true;
    public FlyObject(){}
    public FlyObject(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
