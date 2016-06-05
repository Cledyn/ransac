package features;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.FeaturesParser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sandra on 2016-06-05.
 */
public class NeighbourhoodAnalyzer {

    private static Logger LOGGER = LoggerFactory.getLogger(NeighbourhoodAnalyzer.class);
    private List<Point> photo1;
    private List<Point> photo2;
    private List<Pair> allPairs;

    public NeighbourhoodAnalyzer(String filePathToFeaturesPic0, String filePathToFeaturesPic1) throws FileNotFoundException {
       LOGGER.info("Parsing images data...");
        photo1 = FeaturesParser.parseFeatures(filePathToFeaturesPic0, 0);
        photo2 = FeaturesParser.parseFeatures(filePathToFeaturesPic1, 1);
        allPairs = Lists.newArrayList();
    }

    public double countDistanceFeatures(Point point1, Point point2) {
        double distance = 0;
        for (int i = 0; i < point1.NUMBER_OF_FEATURES; i++) {
            distance += Math.pow((point1.getFeatures()[i] - point2.getFeatures()[i]), 2);
        }
        return Math.sqrt(distance);
    }

    public double countDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow((point1.getX() - point2.getX()), 2) + Math.pow((point1.getY() - point2.getY()), 2));
    }

    public void findClosestNeighbour(List<Point> points, Point point) {
        double minDistance = Double.MAX_VALUE;
        Point closestNeighbour = null;
        for (Point neighbour : points) {
            if (!point.equals(neighbour)) {
                double distance = countDistanceFeatures(point, neighbour);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestNeighbour = neighbour;
                }
            }
        }
        point.setNeighbour(closestNeighbour);
    }

    private void setPointsNeighbours(List<Point> pointsOnB, Point pointOnA) {
        findClosestNeighbour(pointsOnB, pointOnA);
    }

    private boolean checkIfPair(Point point1, Point point2) {
        return point1.getNeighbour().equals(point2) && point2.getNeighbour().equals(point1);
    }

    private boolean pairAlreadyExists(Pair pair) {
        return allPairs.contains(pair);
    }

    public List<Pair> makePairs() {
        Preconditions.checkArgument(photo1.size() == photo2.size(), "Number of pointes must be the same on both lists!");
        for (Point pointOnA : photo1) {
            setPointsNeighbours(photo2, pointOnA);
        }
        for (Point pointOnB : photo2) {
            setPointsNeighbours(photo1, pointOnB);
        }

        for (Point point : photo1) {
            if (checkIfPair(point, point.getNeighbour())) {
                Pair newPair = new Pair(point, point.getNeighbour());
                if (!pairAlreadyExists(newPair)) {
                    allPairs.add(newPair);
                }
            }
        }
        return allPairs;
    }


    public void findNeighbourhood(int numberOfNeighbours, final Point point) {
        List<Point> points = null;
        if (point.getPhotoNo() == 0) {//szukaj sąsiadów na liście punktów pierwszego obrazka
            points = photo1.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        } else { //szukaj sąsiadów na liście punktów drugiego obrazka
            points = photo2.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        }
        points.sort(Comparator.comparing(p -> countDistance(point, p)));
        point.setNeighbourhood(points.subList(0, numberOfNeighbours));
//        System.out.println(Arrays.toString(point.getNeighbourhood().toArray()));
    }

    //    private void setNeighbourhoodForPhotos(List<Point> pointsOnPhotoA, List<Point> pointsOnPhotoB, int numberOfNeighbours ){
//        findNeighbourhood(point);
//    }
    //todo normalizacja erroru!!
    //todo: nie trzeba normalizować. To chyba nie działa zbyt dobrze, ale nie wiem czemu (during invastigation)
    public List<Pair> getConsistentPairsAmongAllPairs(int numberOfNeighbours, double consistencyLimit) {

        List<Pair> pairs = makePairs();
        List<Pair> consistentPairs = new ArrayList<Pair>();
        int matchingPointsInHeighbourhood = 0;

        //nadanie sąsiedztwa każdemu punktowi z pary
        for (Pair pair : pairs) {
            Preconditions.checkArgument(pair.getPoint1().getPhotoNo() != pair.getPoint2().getPhotoNo(), "Points in pair must have been matched on different pictures!");
            findNeighbourhood(numberOfNeighbours, pair.getPoint1());
            findNeighbourhood(numberOfNeighbours, pair.getPoint2());
        }

        for (Pair pair : pairs) {
            matchingPointsInHeighbourhood = 0;
            for (int i = 0; i < pair.getPoint1().getNeighbourhood().size(); i++) {
                if (pair.getPoint2().getNeighbourhood().contains(pair.getPoint1().getNeighbourhood().get(i))) {
                    matchingPointsInHeighbourhood++;
                }
            }
            Preconditions.checkArgument(matchingPointsInHeighbourhood <= numberOfNeighbours, "Cannot be more matiching points than neighbourhood size!");
            double res =(double) matchingPointsInHeighbourhood / numberOfNeighbours;
            LOGGER.info("Matching points {}. Hit {}",matchingPointsInHeighbourhood, res);

            if ((double)matchingPointsInHeighbourhood / numberOfNeighbours >= consistencyLimit) {
                LOGGER.info("Consistent pair found!");
                consistentPairs.add(pair);
            }
        }
        return consistentPairs;


    }

    public List<Pair> analyze(){
        makePairs();
        return allPairs;

    }


    // sprawdzam czy dziala findNeighbours - wyglada na to ze dziala :>>
    public static void main(String[] args) {
        ConsistensyAnalizer consistensyAnalizer = new ConsistensyAnalizer();
        Point p1 = new Point(1, 2, null);
        Point p2 = new Point(1, 1, null);
        Point p3 = new Point(1, 7, null);
        Point p4 = new Point(1, 4, null);
        Point p5 = new Point(0.5, 1.5, null);
        List<Point> list = new ArrayList<Point>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        consistensyAnalizer.findNeighbourhood(list, 2, p1, 0);
    }
}

