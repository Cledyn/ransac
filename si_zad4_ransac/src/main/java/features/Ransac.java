package features;

import model.Pair;
import model.Point;

import java.util.List;

/**
 * Created by Martyna on 04.06.2016.
 */
public class Ransac implements Algorithm {
    public double countDistanceFeatures(Point point1, Point point2) {
        return 0;
    }

    @Override
    public void findNeighbourhood(List<Point> points, int numberOfNeighbours, Point point, double error) {

    }

    public void findClosestNeighbour(List<Point> points, Point point) {

    }

    public List<Pair> makePairs(List<Point> points) {
        return null;
    }
}
