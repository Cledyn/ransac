package features;

import model.Pair;
import model.Point;

import java.util.List;

/**
 * Created by Sandra on 2016-06-04.
 */
public interface Algorithm {

    double countDistance(Point point1, Point point2);
    void findNeighbourhood(List<Point> points, int numberOfNeighbours, Point point);
    void findClosestNeighbour(List<Point> points, Point point);
    List<Pair> makePairs(List<Point> points);

}
