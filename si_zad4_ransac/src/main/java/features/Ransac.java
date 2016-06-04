package features;

import model.Pair;
import model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandra on 2016-06-04.
 */
public class Ransac {


    private double countDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    private void findClosestNeighbour(List<Point> points, Point point){
        double minDistance = Double.MAX_VALUE;
        Point closestNeighbour = null;
        for(Point neighbour:points){
            if(!point.equals(neighbour)){
                double distance = countDistance(point.getX(), neighbour.getX(), point.getY(), neighbour.getY());
                if(distance<minDistance){
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

    private boolean checkIfPair(Point point1, Point point2){
        return point1.getNeighbour().equals(point2) && point2.getNeighbour().equals(point1);
    }

    private List<Pair> makePairs(List<Point> points){
        setPointsNeighbours(points);

        List<Pair> pairs = new ArrayList<Pair>();
        for (Point point : points) {
            if(checkIfPair(point, point.getNeighbour())){
                pairs.add(new Pair(point, point.getNeighbour()));
            }
        }

        return pairs;
    }
}
