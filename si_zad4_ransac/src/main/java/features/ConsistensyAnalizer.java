package features;

import model.Pair;
import model.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sandra on 2016-06-04.
 */
public class ConsistensyAnalizer implements  Algorithm{

    public double countDistanceFeatures(Point point1, Point point2) {
        double distance = 0;
        for (int i = 0; i < point1.NUMBER_OF_FEATURES; i++) {
            distance += Math.pow((point1.getFeatures()[i] - point2.getFeatures()[i]), 2);
        }
        return Math.sqrt(distance);
    }

    public double countDistance(Point point1, Point point2){
        return Math.sqrt(Math.pow((point1.getX()-point2.getX()),2)+Math.pow((point1.getY()-point2.getY()),2));
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

    public void findNeighbourhood(List<Point> points, int numberOfNeighbours, final Point point, double error) {
        List<Point> points1 = points.stream().filter(p->!p.equals(point)).collect(Collectors.toCollection(ArrayList<Point>::new));
        points1.sort(Comparator.comparing(p-> countDistance(point, p)));
        point.setNeighbourhood(points1.subList(0, numberOfNeighbours));
        System.out.println(Arrays.toString(point.getNeighbourhood().toArray()));
    }

    //todo normalizacja erroru!!
    public void analize(List<Point> points, List<Pair> pairs, int numberOfNeighbours,  double error, double consistencyLimit){

        List<Pair> consistentPairs = new ArrayList<>();
        int matchingPointsInHeighbourhood = 0;

        for(Pair pair:pairs){
            findNeighbourhood(points, numberOfNeighbours, pair.getPoint1(), error);
            findNeighbourhood(points, numberOfNeighbours, pair.getPoint2(), error);
        }

        for(Pair pair:pairs){
            matchingPointsInHeighbourhood = 0;
            for(int i = 0; i<pair.getPoint1().getNeighbourhood().size(); i++){
                if(pair.getPoint2().getNeighbourhood().contains(pair.getPoint1().getNeighbourhood().get(i))){
                    matchingPointsInHeighbourhood++;
                }
            }
            if(matchingPointsInHeighbourhood/numberOfNeighbours>=consistencyLimit){
                consistentPairs.add(pair);
            }
        }


    }

    // sprawdzam czy dziala findNeighbours - wyglada na to ze dziala :>>
    public static void main(String[] args) {
        ConsistensyAnalizer consistensyAnalize = new ConsistensyAnalizer();
        Point p1 = new Point(1,2,null);
        Point p2 = new Point(1,1,null);
        Point p3 = new Point(1,7,null);
        Point p4 = new Point(1,4,null);
        Point p5 = new Point(0.5,1.5,null);
        List<Point> list = new ArrayList<Point>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        consistensyAnalize.findNeighbourhood(list, 2, p1, 0);
    }
}