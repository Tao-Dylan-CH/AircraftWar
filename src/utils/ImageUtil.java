package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author 挚爱之夕
 * @date 2022-01-29 - 01 - 29 - 22:51
 * @Description 图片访问工具类，包装成jar包时，最初的方法找不到文件，出于路径问题使用URL
 * @Version 1.0
 */
public class ImageUtil {
//    public static String path = "src/images/";
//    public static BufferedImage getImage(String fileName){
//        BufferedImage bufferedImage = null;
//        try {
//            bufferedImage = ImageIO.read(new File(path + fileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bufferedImage;
//    }
    public static String path = "/images/";
    //获取图片
    public static BufferedImage getImage(String fileName){
        URL url = ImageUtil.class.getResource(path + fileName);
        BufferedImage bufferedImage = null;
        try {
            assert url != null;
            bufferedImage = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
    //获取图标
    public static ImageIcon getIcon(String fileName){
        URL url = ImageUtil.class.getResource(path + fileName);
        ImageIcon imageIcon = null;
        imageIcon = new ImageIcon(url);
        return imageIcon;
    }
}
