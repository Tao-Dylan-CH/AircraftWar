package dao;


import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author 挚爱之夕
 * @date 2022-01-31 - 01 - 31 - 16:48
 * @Description
 * @Version 1.0
 */
public class AudioDao {
    private AudioInputStream stream;//音频输入流
    private AudioFormat format;//音频格式
    private DataLine.Info info;//音频行信息
    private Clip clip;//音频夹
    //背景音乐:向外界提供该对象用于暂停或播放
    public static AudioDao backgroundMusic;
    /**
     * 点击音效
     */
    public static void playClickSound(){
        if(!MessageDao.isSelectPlayClickSoundEffect){//用户选择不播放
            return;
        }
        //'/'表示绝对资源位置，考虑到要包装为jar包，获取资源资源使用这种方式
        URL url = AudioDao.class.getResource("/sounds/ClickSound.wav");
        AudioDao audioDao = new AudioDao();
        audioDao.open(url);
        audioDao.load();
        audioDao.start();
    }
    /**
     * 爆炸音效
     */
    public static void playBreakSound(){
        if(!MessageDao.isSelectPlayBombSoundEffect){
            return;
        }
        URL url = AudioDao.class.getResource("/sounds/Break.wav");
        AudioDao audioDao = new AudioDao();
        audioDao.open(url);
        audioDao.load();
        audioDao.start();
    }

    /**
     * 背景音乐
     */
    public static void playBackgroundMusic(){
        URL url = AudioDao.class.getResource("/sounds/backgroundMusic.mid");
        backgroundMusic = new AudioDao();//通过向外界提供该实例，用于播放或暂停
        backgroundMusic.open(url);
        backgroundMusic.load();
        backgroundMusic.loop();
        backgroundMusic.start();
    }
    public static void shutDownBackgroundMusic(){
        if(backgroundMusic != null){
            backgroundMusic.stop();
        }
    }
    public static void openBackgroundMusic(){
        if(backgroundMusic != null){
            backgroundMusic.start();
        }
    }
    /**
     * 我方爆炸
     */
    public static void playHeroExplodeSound(){
        AudioDao audioDao = new AudioDao();
        URL url = AudioDao.class.getResource("/sounds/HeroExplodeSound.wav");
        audioDao.open(url);
        audioDao.load();
        audioDao.start();
    }
    /**
     * 打开声音文件方法
     * @param url 文件路径
     */
    public void open(URL url) {
//        file = new File(path);//音频文件对象
        try {
            stream = AudioSystem.getAudioInputStream(url);//音频输入流
            format = stream.getFormat();//音频格式对象
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立播放音频的音频行
     */
    public void load() {
        info = new DataLine.Info(Clip.class, format);//音频行信息
        try {
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);//将音频数据读入音频行
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        clip.stop();//暂停音频播放
    }

    public void start() {
        clip.start();//播放音频
    }

    public void loop() {
        clip.loop(20);//回放
    }
}
