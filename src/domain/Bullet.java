package domain;

import dao.Recorder;

/**
 * @author 挚爱之夕
 * @date 2022-01-30 - 01 - 30 - 22:16
 * @Description 子弹
 * @Version 1.0
 */
public class Bullet extends FlyObject implements Runnable{
    private Direction direction;

    public Bullet(int x, int y, Direction direction){
        this.x = x;
        this.y = y;
        this.direction = direction;
        isLive = true;
    }
    public Bullet(){}
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
            if(direction == Direction.UP){
                y -= speed;
            }
            if(direction == Direction.DOWN){
                y += speed;
            }
            if(direction == Direction.RIGHT){
                x += speed;
            }
            if(y < 0 || y > 1000 || x > 600){
                isLive = false;
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
