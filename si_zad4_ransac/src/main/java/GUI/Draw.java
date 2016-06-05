package GUI;

import com.google.common.collect.Lists;
import features.NeighbourhoodAnalyzer;
import model.Pair;
import model.Photo;
import model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Martyna on 05.06.2016.
 */
public class Draw extends JFrame {

    private static Logger LOGGER = LoggerFactory.getLogger(Draw.class);
    private Photo photo1;
    private Photo photo2;
    private final static String FILE1_FEATURES_FILEPATH = "D:\\Studenckie\\sem6\\projects\\ransac\\si_zad4_ransac\\src\\main\\resources\\d1.png.haraff.sift";
    private final static String FILE2_FEATURES_FILEPATH = "D:\\Studenckie\\sem6\\projects\\ransac\\si_zad4_ransac\\src\\main\\resources\\d2.png.haraff.sift";
    private final static Color[] COLORS = {Color.ORANGE, Color.GRAY, Color.GREEN, Color.RED, Color.BLACK, Color.BLUE, Color.MAGENTA, Color.white};
    private final static Random rnd = new Random();

    public Draw(String title) throws HeadlessException, FileNotFoundException {
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
    public void paint(Graphics g) {
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
        this.setSize(image.getWidth() + image2.getWidth(), image.getHeight());
        paintLines(g2);
//        g2.draw(new Line2D.Double(100.0,100.0,700.0,400.0));
//        g2.draw(new Ellipse2D.Double(90.0, 90.0, 20.0, 20.0));
//        g2.draw(new Ellipse2D.Double(690.0, 390.0, 20.0, 20.0));
    }

//todo: old one
//    private void paintLines(Graphics2D g2){
//        for(Pair pair:photo1.getPairs()){
//            double x1 = pair.getPoint1().getX()*this.getWidth()/100;
//            double x2 = pair.getPoint2().getX()*this.getWidth()/100;
//            double y1 = pair.getPoint1().getY()*this.getWidth()/100;
//            double y2 = pair.getPoint2().getY()*this.getWidth()/100;
//            Shape line = new Line2D.Double(x1, y1, x2, y2);
//            g2.setColor(Color.ORANGE);
//            g2.draw(line);
//        }
//    }

    private void paintLines(Graphics2D g2) {
        for (Pair pair : photo1.getPairs()) {
            double x1 = pair.getPoint1().getX();
            double x2 = pair.getPoint2().getX();
            double y1 = pair.getPoint1().getY();
            double y2 = pair.getPoint2().getY();
            Shape line = new Line2D.Double(x1, y1, x2, y2);
            int colorInd = (int) Math.floor(rnd.nextDouble() * COLORS.length);
            g2.setColor(COLORS[colorInd]);
            g2.draw(line);
        }
    }

    public void setPhoto1(Photo photo1) {
        this.photo1 = photo1;
    }

    public void setPhoto2(Photo photo2) {
        this.photo2 = photo2;
    }

    public static List<Pair> moveSdPointCoordinates(List<Pair> allPairs, double xShift, double yShift) {
        List<Pair> shiftedPoints = Lists.newCopyOnWriteArrayList(allPairs);
        for (Pair shiftedPair : shiftedPoints) {
            Point snd = shiftedPair.getPoint2();
            double oldX = snd.getX();
            snd.setX(oldX + xShift);
        }
        return shiftedPoints;

    }

    public static void main(String[] args) throws FileNotFoundException {
        NeighbourhoodAnalyzer analyzer = new NeighbourhoodAnalyzer(FILE1_FEATURES_FILEPATH, FILE1_FEATURES_FILEPATH);
        List<Pair> allPairsMake = analyzer.makePairs();
        List<Pair> allPairs = Draw.moveSdPointCoordinates(allPairsMake,400, 300);
//       List<Pair> allPairsAnalyzer = analyzer.makePairs();
//        LOGGER.info("All pairs size {}", allPairsAnalyzer.size());
//        List<Pair> consistentPairs = analyzer.getConsistentPairsAmongAllPairs(16,0.1);
//        LOGGER.info("consistent pairs size {}", consistentPairs.size());
//        for(Pair pair : consistentPairs){
//            LOGGER.info("Consistent pair: {}",pair.toString());
//        }
//        List<Pair> allPairs = Draw.moveSdPointCoordinates(consistentPairs,400, 300);
//        for (Pair pair : allPairs){
//            LOGGER.info("All afte cons {}",pair.toString());
//        }
//        model.Point p1 = new model.Point(10.0,20.0);
//        model.Point p2 = new model.Point(10.9,10.1);
//        model.Point p3 = new model.Point(20.3,70.44);
//        model.Point p4 = new model.Point(60,40);
//
//                model.Point p1 = new model.Point(56.4181, 164.858);
//        model.Point p2 = new model.Point(104.286, 164.452);
//        model.Point p3 = new model.Point(456.0006, 465.39300000000003);
//        model.Point p4 = new model.Point(503.98900000000003, 464.023);
//        java.util.List<Pair> allPairs = new ArrayList<Pair>();
//        allPairs.add(new Pair(p1, p3));
//        allPairs.add(new Pair(p2, p4));

        for (Pair single : allPairs) {
            System.out.println("Single: " + " " + single.getPoint1().toString() + " " + single.getPoint2().toString());
        }
        Photo ph1 = new Photo(new File("D:\\Studenckie\\sem6\\projects\\ransac\\si_zad4_ransac\\src\\main\\resources\\d1.png"),
                null, allPairs);
        Photo ph2 = new Photo(new File("D:\\Studenckie\\sem6\\projects\\ransac\\si_zad4_ransac\\src\\main\\resources\\d2.png"),
                null, allPairs);
        Draw draw = new Draw("test");
        draw.setPhoto1(ph1);
        draw.setPhoto2(ph2);

    }
}
