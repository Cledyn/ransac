package features;

import model.Pair;
import model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandra on 2016-06-04.
 */
public class Ransac implements  Algorithm{


    public double countDistance(Point point1, Point point2) {
        double distance = 0;
        for (int i = 0; i < point1.NUMBER_OF_FEATIRES; i++) {
            distance += Math.pow((point1.getFeatures()[i] - point2.getFeatures()[i]), 2);
        }
        return Math.sqrt(distance);
    }


    public void findClosestNeighbour(List<Point> points, Point point) {
        double minDistance = Double.MAX_VALUE;
        Point closestNeighbour = null;
        for (Point neighbour : points) {
            if (!point.equals(neighbour)) {
                double distance = countDistance(point, neighbour);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestNeighbour = neighbour;
                }
            }
        }
        point.setNeighbour(closestNeighbour);
    }

    private void setPointsNeighbours(List<Point> points) {
        for (Point point : points) {
            findClosestNeighbour(points, point);
        }
    }

    private boolean checkIfPair(Point point1, Point point2) {
        return point1.getNeighbour().equals(point2) && point2.getNeighbour().equals(point1);
    }

    public List<Pair> makePairs(List<Point> points) {
        setPointsNeighbours(points);

        List<Pair> pairs = new ArrayList<Pair>();
        for (Point point : points) {
            if (checkIfPair(point, point.getNeighbour())) {
                pairs.add(new Pair(point, point.getNeighbour()));
            }
        }

        return pairs;
    }

    public void findNeighbourhood(List<Point> points, int numberOfNeighbours, Point point) {

    }
}
