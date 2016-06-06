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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class analyze pairs of corresponding points on separate images and looks for closes neighbours for all paired points
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

    public NeighbourhoodAnalyzer() {
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
//        Preconditions.checkArgument(photo1.size() == photo2.size(), "Number of pointes must be the same on both lists!");
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
        filterPointsOnPhotosOnlyHavingPair();
        if (point.getPhotoNo() == 0) {//szukaj sąsiadów na liście punktów pierwszego obrazka
            points = photo1.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        } else { //szukaj sąsiadów na liście punktów drugiego obrazka
            points = photo2.stream().filter(p -> !p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        }
        points.sort(Comparator.comparing(p -> countDistance(point, p)));
        point.setNeighbourhood(points.subList(0, numberOfNeighbours));
        LOGGER.info("Point found neighbours {}", Arrays.toString(point.getNeighbourhood().toArray()));
    }

    private void filterPointsOnPhotosOnlyHavingPair() {
        List<Point> photo1filtered = new ArrayList<>();
        List<Point> photo2filtered = new ArrayList<>();
        for (Pair pair : allPairs) {
            if (pair.getPoint1().getPhotoNo() == 0) {
                photo1filtered.add(pair.getPoint1());
            }
            if (pair.getPoint1().getPhotoNo() == 1) {
                photo2filtered.add(pair.getPoint1());
            }
            if (pair.getPoint2().getPhotoNo() == 0) {
                photo1filtered.add(pair.getPoint2());
            }
            if (pair.getPoint2().getPhotoNo() == 1) {
                photo2filtered.add(pair.getPoint2());
            }
        }
        photo1 = photo1filtered;
        photo2 = photo2filtered;
    }

    //todo normalizacja erroru!!
    //todo: nie trzeba normalizować. To chyba nie działa zbyt dobrze, ale nie wiem czemu (during invastigation)
    public List<Pair> getConsistentPairsAmongAllPairs(int numberOfNeighbours, double consistencyLimit) {

        List<Pair> pairs = makePairs();
        List<Pair> consistentPairs = new ArrayList<Pair>();
        int matchingPointsInHeighbourhood = 0;

        //nadanie sąsiedztwa każdemu punktowi z pary
        for (Pair pair : pairs) {
//            Preconditions.checkArgument(pair.getPoint1().getPhotoNo() != pair.getPoint2().getPhotoNo(), "Points in pair must have been matched on different pictures!");
            findNeighbourhood(numberOfNeighbours, pair.getPoint1());
            findNeighbourhood(numberOfNeighbours, pair.getPoint2());
//            LOGGER.info("Found neighbours point 1 : {}",pair.getPoint1().getNeighbourhood().size());
//            LOGGER.info("Found neighbours point 2 : {}",pair.getPoint2().getNeighbourhood().size());
        }

        for (Pair pair : pairs) {
            int matchingPointsInNeighbourhood = getNeighboursMatchInPair(pair);
            Preconditions.checkArgument(matchingPointsInNeighbourhood <= numberOfNeighbours, "Cannot be more matiching points than neighbourhood size!");
            double res = (double)matchingPointsInNeighbourhood / (double)numberOfNeighbours;
            LOGGER.info("Matching points {}. Number of neighbours {}. Hit {}", matchingPointsInNeighbourhood, numberOfNeighbours, res);

            if ((double) matchingPointsInNeighbourhood / numberOfNeighbours >= consistencyLimit) {
                LOGGER.info("Consistent pair found!");
                consistentPairs.add(pair);
            }
        }
        LOGGER.info("FOUND PAIRS {}", consistentPairs.size());
        return consistentPairs;
    }

    private int getNeighboursMatchInPair(Pair pair) {
        int matchingPointsInNeighbourhood = 0;
//        LOGGER.info("Point 1 neighbours {}", Arrays.toString(pair.getPoint1().getNeighbourhood().toArray()));
//        LOGGER.info("Point 2 neighbours {}", Arrays.toString(pair.getPoint2().getNeighbourhood().toArray()));
        for (Point neighbourP1 : pair.getPoint1().getNeighbourhood()) {
//            LOGGER.info("Neighbour: {}", neighbour);
//            LOGGER.info("Neighbour neighbour: {}", Arrays.toString(neighbour.getNeighbourhood().toArray()));
            if (isMatchForPoints(pair.getPoint2(), neighbourP1)) {
                matchingPointsInNeighbourhood++;
            }
        }
        LOGGER.info("Matching points getNeighbourMatchInPair {}", matchingPointsInNeighbourhood);
        return matchingPointsInNeighbourhood;
    }

    // point2 to jest naighbour punktu 1 trzeba sprawdzić czy point1 jest neighbourem punktu 2
    private boolean isMatchForPoints(Point point2, Point neighbourP1) {
        for (Point neighbourP2 : point2.getNeighbourhood()) {
//            LOGGER.info("(isMatch) : {} - {}", neighbourP2, neighbourP1.getNeighbour());
//            neighbour.getX() == point1.getX() && neighbour.getY() == point1.getY()
            if (neighbourP1.getX() == neighbourP2.getNeighbour().getX() && neighbourP1.getY() == neighbourP2.getNeighbour().getY()) {
                LOGGER.info("** Neighbour ISMATCH **");
                return true;
            }
        }
        return false;
    }
}

