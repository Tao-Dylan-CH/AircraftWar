package dao;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 挚爱之夕
 * @date 2022-02-05 - 02 - 05 - 14:59
 * @Description bean - 对应数据库中的表，表示一行数据
 * @Version 1.0
 */
public class PlayerRecord {
    int id;
    String name;
    int score;
    int gameTime;
    String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return name + "\t" + score+ "\t" + gameTime + "s" + "\t" + time;
    }
}
