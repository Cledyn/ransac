package GUI;

import model.Pair;
import model.Photo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Martyna on 05.06.2016.
 */
public class Draw extends JFrame{

    private Photo photo1;
    private Photo photo2;

    public Draw(String title) throws HeadlessException {
        super(title);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public Draw(String title, Photo photo1, Photo photo2) throws HeadlessException {
        super(title);
        this.photo1 = photo1;
        this.photo2 = photo2;
    }

    // wspolrzedne trzeba wyznaczac wzgledem gornego lewego wierzcholka
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage image = null;
        BufferedImage image2 = null;
        try {
            // musi byc pelna sciezka do obrazka
            image = ImageIO.read(photo1.getFilePath());
            image2 = ImageIO.read(photo2.getFilePath());
        } catch (IOException e) {
            System.out.println("unable to read photo");
            e.printStackTrace();
        }
        g2.drawImage(image, 10, 10, this);
        g2.drawImage(image2, image.getWidth(), 10, this);
        this.setSize(image.getWidth()+image2.getWidth(), image.getHeight());
        paintLines(g2);
//        g2.draw(new Line2D.Double(100.0,100.0,700.0,400.0));
//        g2.draw(new Ellipse2D.Double(90.0, 90.0, 20.0, 20.0));
//        g2.draw(new Ellipse2D.Double(690.0, 390.0, 20.0, 20.0));
    }

    private void paintLines(Graphics2D g2){
        for(Pair pair:photo1.getPairs()){
            double x1 = pair.getPoint1().getX()*this.getWidth()/100;
            double x2 = pair.getPoint2().getX()*this.getWidth()/100;
            double y1 = pair.getPoint1().getY()*this.getWidth()/100;
            double y2 = pair.getPoint2().getY()*this.getWidth()/100;
            Shape line = new Line2D.Double(x1, y1, x2, y2);
            g2.setColor(Color.ORANGE);
            g2.draw(line);
        }
    }

    public void setPhoto1(Photo photo1) {
        this.photo1 = photo1;
    }

    public void setPhoto2(Photo photo2) {
        this.photo2 = photo2;
    }

    public static void main(String[] args) {
        model.Point p1 = new model.Point(10,20,null);
        model.Point p2 = new model.Point(10,10,null);
        model.Point p3 = new model.Point(20,70,null);
        model.Point p4 = new model.Point(60,40,null);
        java.util.List<Pair> pairs = new ArrayList<Pair>();
        pairs.add(new Pair(p1, p3));
        pairs.add(new Pair(p2, p4));
        Photo ph1 = new Photo(new File("F:\\PWr\\VI_SEMESTR\\SIiW\\RANSAC\\ransac\\si_zad4_ransac\\src\\main\\resources\\wypijmy.png"),
                null, pairs);
        Photo ph2 = new Photo(new File("F:\\PWr\\VI_SEMESTR\\SIiW\\RANSAC\\ransac\\si_zad4_ransac\\src\\main\\resources\\wypijmy.png"),
                null, pairs);
        Draw draw = new Draw("test");
        draw.setPhoto1(ph1);
        draw.setPhoto2(ph2);

    }
}
