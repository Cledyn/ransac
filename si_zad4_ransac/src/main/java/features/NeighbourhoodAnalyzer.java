package features;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import parser.FeaturesParser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sandra on 2016-06-05.
 */
public class NeighbourhoodAnalyzer {

    private List<Point> photo1;
    private List<Point> photo2;
    private List<Pair> allPairs;

    public NeighbourhoodAnalyzer(String filePathToFeaturesPic0, String filePathToFeaturesPic1) throws FileNotFoundException {
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
            if (!point.equals(neighbour)) { //todo: to chyba tu nie potrzebne, skoro points to lista punktów na obrazie B, a point - punkt na obrazie A
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


    //todo: new version
    //todo: remove possibility of redundant pairs on list --> when to list is added new Pair(point, point.getNeighbour()) and new Pair(point.getNeighbour(), point.getNeighbour().getNeighbour())
    public void makePairs2(List<Point> pointsOnPhoto1, List<Point> pointsOnPhoto2) {
        Preconditions.checkArgument(pointsOnPhoto1.size() == pointsOnPhoto2.size(), "Number of pointes must be the same on both lists!");
        for (Point pointOnA : pointsOnPhoto1) {
            setPointsNeighbours(pointsOnPhoto2, pointOnA);
        }
        for (Point pointOnB : pointsOnPhoto2) {
            setPointsNeighbours(pointsOnPhoto1, pointOnB);
        }

        for (Point point : pointsOnPhoto1) {
            if (checkIfPair(point, point.getNeighbour())) {
                Pair newPair = new Pair(point, point.getNeighbour());
                if (!pairAlreadyExists(newPair)) {
                    allPairs.add(new Pair(point, point.getNeighbour()));
                }
            }
        }
    }

    public void findNeighbourhood(int numberOfNeighbours, final Point point, double error) {
        List<Point> points = null;
        if (point.getPhotoNo() == 0) {//szukaj sąsiadów na liście punktów pierwszego obrazka
            points = photo1.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        } else { //szukaj sąsiadów na liście punktów drugiego obrazka
            points = photo2.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        }
        points.sort(Comparator.comparing(p -> countDistance(point, p)));
        point.setNeighbourhood(points.subList(0, numberOfNeighbours));
        System.out.println(Arrays.toString(point.getNeighbourhood().toArray()));
    }

    //    private void setNeighbourhoodForPhotos(List<Point> pointsOnPhotoA, List<Point> pointsOnPhotoB, int numberOfNeighbours ){
//        findNeighbourhood(point);
//    }
    //todo normalizacja erroru!!
    public List<Pair> getConsistentPairsAmongAllPairs(List<Pair> pairs, int numberOfNeighbours, double error, double consistencyLimit) {

        List<Pair> consistentPairs = new ArrayList<Pair>();
        int matchingPointsInHeighbourhood = 0;

        //nadanie sąsiedztwa każdemu punktowi z pary
        for (Pair pair : pairs) {
            Preconditions.checkArgument(pair.getPoint1().getPhotoNo() != pair.getPoint2().getPhotoNo(), "Points in pair must have been matched on different pictures!");
            findNeighbourhood(numberOfNeighbours, pair.getPoint1(), error);
            findNeighbourhood(numberOfNeighbours, pair.getPoint2(), error);
        }

        //to jest dobrze napisane xD
        for (Pair pair : pairs) {
            matchingPointsInHeighbourhood = 0;
            for (int i = 0; i < pair.getPoint1().getNeighbourhood().size(); i++) {
                if (pair.getPoint2().getNeighbourhood().contains(pair.getPoint1().getNeighbourhood().get(i))) {
                    matchingPointsInHeighbourhood++;
                }
            }
            Preconditions.checkArgument(matchingPointsInHeighbourhood <= numberOfNeighbours, "Cannot be more matiching points than neighbourhood size!");
            if (matchingPointsInHeighbourhood / numberOfNeighbours >= consistencyLimit) {
                consistentPairs.add(pair);
            }
        }
        return consistentPairs;


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

