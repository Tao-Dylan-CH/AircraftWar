package domain;

import dao.Recorder;
import utils.ImageUtil;

import java.util.Random;

/**
 * @author 挚爱之夕
 * @date 2022-02-01 - 02 - 01 - 8:54
 * @Description domain
 * @Version 1.0
 */
public class Food extends Bullet implements Runnable{
    private FoodType type;
    public Food(){}
    public Food(FoodType type){
        Random random = new Random();
        this.type = type;
        //增益类型
        switch (type){
            case RED_BULLET:
                image = ImageUtil.getImage("goods_red_bullet.png");
                break;
            case LIFE:
                image = ImageUtil.getImage("goods_life.png");
                break;
            case PURPLE_BULLET:
                image = ImageUtil.getImage("goods_purple_bullet.png");
                break;
            case IncreasedFiringSpeed:
                image = ImageUtil.getImage("goods_missile.png");
                break;
        }
        //随机坐标
        this.x = random.nextInt(600 - image.getWidth());
        this.y = 0;
        speed = 10;
        //启动线程
        new Thread(this).start();
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
            if(Recorder.gameIsStop){
                continue;
            }
            y += speed;
            if(y > 1000){
                isLive = false;
            }

        }
    }

    public FoodType getType() {
        return type;
    }

    public void setType(FoodType type) {
        this.type = type;
    }
}
