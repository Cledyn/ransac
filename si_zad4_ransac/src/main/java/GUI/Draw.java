package GUI;

import com.google.common.collect.Lists;
import features.RansacAfinic;
import model.Pair;
import model.Photo;
import model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImgSizeRetriever;

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
 * This class is responsible for visualisation both images of the same object in different perspective and how matching points
 */
public class Draw extends JFrame {

    private static Logger LOGGER = LoggerFactory.getLogger(Draw.class);
    private Photo photo1;
    private Photo photo2;
    private final static String FILE1_FEATURES_FILEPATH = ImgSizeRetriever.class.getClassLoader().getResource("k1.png.haraff.sift").getFile();
    private final static String FILE2_FEATURES_FILEPATH = ImgSizeRetriever.class.getClassLoader().getResource("k2.png.haraff.sift").getFile();
    private final static Color[] COLORS = {Color.ORANGE, Color.GRAY, Color.GREEN, Color.RED, Color.BLACK, Color.BLUE, Color.MAGENTA, Color.white};
    private final static Random rnd = new Random();
    public static int xShift = 400;

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
        this.setSize(image2.getWidth() + xShift, image.getHeight());
        paintLines(g2);
//        g2.draw(new Line2D.Double(100.0,100.0,700.0,400.0));
//        g2.draw(new Ellipse2D.Double(90.0, 90.0, 20.0, 20.0));
//        g2.draw(new Ellipse2D.Double(690.0, 390.0, 20.0, 20.0));
    }


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

    public static List<Pair> moveSdPointCoordinates(List<Pair> allPairs) {
        List<Pair> shiftedPoints = Lists.newCopyOnWriteArrayList(allPairs);
        for (Pair shiftedPair : shiftedPoints) {
            Point snd = shiftedPair.getPoint2();
            double oldX = snd.getX();
            snd.setX(oldX + xShift + 10);
//            snd.setX(oldX + 10);
            snd.setY(snd.getY() + 10);
        }
        return shiftedPoints;

    }

//    public static void main(String[] args) throws FileNotFoundException {
//        NeighbourhoodAnalyzer analyzer = new NeighbourhoodAnalyzer(FILE1_FEATURES_FILEPATH, FILE2_FEATURES_FILEPATH);
////        NeighbourhoodAnalyzer analyzer = new NeighbourhoodAnalyzer("kubek1.png.haraff.sift", FILE2_FEATURES_FILEPATH);
//        List<Pair> allPairsMake = analyzer.makePairs();
//        LOGGER.info("All pairs size {}", allPairsMake.size());
//        List<Pair> consistentPairs = analyzer.getConsistentPairsAmongAllPairs(50,0.50);
//        List<Pair> allPairs = Draw.moveSdPointCoordinates(consistentPairs);
//        Photo ph1 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("k1.png").getFile()),
//                null, allPairs);
//        Photo ph2 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("k2.png").getFile()),
//                null, allPairs);
//        Draw draw = new Draw("test");
//        draw.setPhoto1(ph1);
//        draw.setPhoto2(ph2);
//
//    }

    public static void main(String[] args) throws FileNotFoundException {
        RansacAfinic ransacAfinic = new RansacAfinic();
        List<Pair> ransacPairs = ransacAfinic.run("k1.png.haraff.sift", "k2.png.haraff.sift", 100, 0.1);
        List<Pair> allPairs = Draw.moveSdPointCoordinates(ransacPairs);
        Photo ph1 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("k1.png").getFile()),
                null, allPairs);
        Photo ph2 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("k2.png").getFile()),
                null, allPairs);
        Draw draw = new Draw("test");
        draw.setPhoto1(ph1);
        draw.setPhoto2(ph2);

    }

}
