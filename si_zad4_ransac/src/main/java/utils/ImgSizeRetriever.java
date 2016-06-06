package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
* This class is to get actual size (width and height) of given picture
 */
public class ImgSizeRetriever {

    private static int [] getImgSize(String filePath) throws MalformedURLException {
        BufferedImage readImage = null;
        int [] imgSize = new int[2];
        try {
            readImage = ImageIO.read(new File(filePath));
            imgSize[0]=readImage.getHeight();
            imgSize[1] = readImage.getWidth();
        } catch (Exception e) {
            readImage = null;
        }
        return imgSize;

    }

    private static int getImgHeight(String filePath) throws MalformedURLException {
        URL url = new URL(filePath);
        Image image = new ImageIcon(url).getImage();
        return image.getHeight(null);
    }

    public static void main(String[] args) throws MalformedURLException {
        int [] img = ImgSizeRetriever.getImgSize("D:\\Studenckie\\sem6\\projects\\ransac\\si_zad4_ransac\\src\\main\\resources\\wypijmy.png");
        System.out.println(String.format("Height %s, width %s",img[0], img[1]));
    }
}
