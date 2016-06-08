package GUI;

import features.RansacAfinic;
import features.RansacPerspective;
import model.Pair;
import model.Photo;
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
    private final static String FILE1_FEATURES_FILEPATH = ImgSizeRetriever.class.getClassLoader().getResource("b1.png.haraff.sift").getFile();
    private final static String FILE2_FEATURES_FILEPATH = ImgSizeRetriever.class.getClassLoader().getResource("b2.png.haraff.sift").getFile();
    private final static Color[] COLORS = {Color.ORANGE, Color.GRAY, Color.GREEN, Color.RED, Color.BLACK, Color.BLUE, Color.MAGENTA, Color.white};
    private final static Random rnd = new Random();
    public static final int EDGE_WIDTH = 10;

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
        int width = image.getWidth();
        int height = image.getHeight();

        g2.drawImage(image, EDGE_WIDTH, EDGE_WIDTH, this);
        g2.drawImage(image2, image.getWidth()+ EDGE_WIDTH, EDGE_WIDTH, this);
//        LOGGER.info("Image size {} x {}", image.getWidth(), image.getHeight());
//        g2.drawImage(image, EDGE_WIDTH, image.getHeight()+ EDGE_WIDTH, this);
//        g2.drawImage(image2, image.getWidth()+ EDGE_WIDTH, image.getHeight()+ EDGE_WIDTH, this);
        this.setSize(image.getWidth() + image.getWidth(), image.getHeight());
        paintLines(g2, image.getWidth(), image.getHeight());
    }


    private void paintLines(Graphics2D g2, int xShift, int yShift) {
        paintPairs(g2, photo1.getPairs(), xShift, 0, Color.LIGHT_GRAY);
        paintPairs(g2, photo1.getFiltered_pairs(), xShift, 0, Color.RED);
    }

    private void paintPairs(Graphics2D g2, List<Pair> pairs, int xShift, int yShift, Color color){
        for (Pair pair : pairs) {
            double x1 = pair.getPoint1().getX() + EDGE_WIDTH;
            double x2 = pair.getPoint2().getX() + xShift + EDGE_WIDTH;
            double y1 = pair.getPoint1().getY() + yShift + EDGE_WIDTH;
            double y2 = pair.getPoint2().getY() + yShift + EDGE_WIDTH;
            Shape line = new Line2D.Double(x1, y1, x2, y2);
//            int colorInd = (int) Math.floor(rnd.nextDouble() * COLORS.length);
            g2.setColor(color);
            g2.draw(line);
        }
    }

    public void setPhoto1(Photo photo1) {
        this.photo1 = photo1;
    }

    public void setPhoto2(Photo photo2) {
        this.photo2 = photo2;
    }

    /*public static void main(String[] args) throws FileNotFoundException {
        NeighbourhoodAnalyzer analyzer = new NeighbourhoodAnalyzer(FILE1_FEATURES_FILEPATH, FILE2_FEATURES_FILEPATH);
        List<Pair> allPairsMake = analyzer.makePairs();
        LOGGER.info("All pairs size {}", allPairsMake.size());
        List<Pair> consistentPairs = analyzer.getConsistentPairsAmongAllPairs(50,0.35);

        Photo ph1 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("kubek1.png").getFile()),
                null, allPairsMake, consistentPairs);
        Photo ph2 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("kubek2.png").getFile()),
                null, allPairsMake, consistentPairs);
        Draw draw = new Draw("test");
        draw.setPhoto1(ph1);
        draw.setPhoto2(ph2);

    }*/

    public static void main(String[] args) throws FileNotFoundException {
//        RansacAfinic ransac = new RansacAfinic();
        RansacPerspective ransac = new RansacPerspective();
        List<Pair> ransacPairs = ransac.run("b1.png.haraff.sift", "b2.png.haraff.sift", 100000, 5);
        LOGGER.info("Found pairs : {}", ransac.getAllPairs().size());
        LOGGER.info("Filtered pairs : {}", ransacPairs.size());
        Photo ph1 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("b1.png").getFile()),
                null, ransac.getAllPairs(), ransacPairs);
        Photo ph2 = new Photo(new File(ImgSizeRetriever.class.getClassLoader().getResource("b2.png").getFile()),
                null, ransac.getAllPairs(), ransacPairs);
        Draw draw = new Draw("test");
        draw.setPhoto1(ph1);
        draw.setPhoto2(ph2);

    }

}
