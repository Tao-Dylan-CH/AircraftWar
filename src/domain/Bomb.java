package domain;

import dao.Recorder;
import utils.ImageUtil;

/**
 * @author 挚爱之夕
 * @date 2022-01-31 - 01 - 31 - 16:10
 * @Description 该类显示爆炸效果
 * @Version 1.0
 */
public class Bomb extends Bullet implements Runnable{
    private int index;
    public Bomb(int x, int y){
        this.x = x;
        this.y = y;
        index = 0;
        image = ImageUtil.getImage("bomb" + (index++) +".png");
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        while(index <= 5){
            if(Recorder.returnToMenu){
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Recorder.gameIsStop){
                continue;
            }
            image = ImageUtil.getImage("bomb" + (index++) +".png");
            if(index == 6){//消亡
                isLive = false;
            }
        }
    }
}
