package features;

import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sandra on 2016-06-07.
 */
public class Ransac {


    private static Logger LOGGER = LoggerFactory.getLogger(Ransac.class);
    protected static final int PARAMETER_MATRIX_DIMENSION = 3;
    Random r = new Random();
    protected List<Pair> allPairs = new ArrayList<>();

    public List<Pair> getAllPairs() {
        return allPairs;
    }

    protected List<Pair> takeRandomPairs(List<Pair> pairs, int pairsToTake) {
        int pairsNo = pairs.size();
        List<Pair> chosen = Lists.newArrayList();
        for (int i = 0; i < pairsToTake; i++) {
            int index = (int) (r.nextDouble() * pairsNo);
//            LOGGER.info("Index"+i+" "+index);
            chosen.add(pairs.get(index));
        }
        return chosen;

    }

    protected static RealMatrix prepareVector(List<Pair> pairs) {
        double[][] vector = new double[pairs.size() * 2][];
        for (int i = 0; i < vector.length; i++) {
            if (i < pairs.size()) {
                vector[i] = new double[]{pairs.get(i).getPoint2().getX()};
            } else {
                vector[i] = new double[]{pairs.get(i - pairs.size()).getPoint2().getY()};
            }
        }
        return new Array2DRowRealMatrix(vector);
    }

    public static RealMatrix getTransform(RealMatrix matrix, RealMatrix vector) {
        RealMatrix matrixInverted;
        try {
            matrixInverted = new LUDecomposition(matrix).getSolver().getInverse();
        } catch (SingularMatrixException ex) {
//            LOGGER.debug("Singular matrix! Return null");
            return null;
        }
        return matrixInverted.multiply(vector);
    }

    public RealMatrix getPointAsVector(Point point) {
        double[][] vectorOnPoint = {{point.getX()}, {point.getY()}, {1}};
        return new Array2DRowRealMatrix(vectorOnPoint);
    }

    public RealMatrix calculateCoordinatesForOtherPic(RealMatrix params, Point point1) {
        RealMatrix point1Matrix = getPointAsVector(point1);
        return params.multiply(point1Matrix);
    }

    public Point getPointFromMatrix(RealMatrix matrix){
        return new Point(matrix.getRow(0)[0],matrix.getRow(1)[0]);
    }

    public Point calculatePoint(RealMatrix params, Point point1){
        return getPointFromMatrix(calculateCoordinatesForOtherPic(params,point1));
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

    public List<Pair> makePairs(List<Point> pointsOnA, List<Point> pointsOnB) {
        List<Pair> allPairs = Lists.newArrayList();
//        Preconditions.checkArgument(photo1.size() == photo2.size(), "Number of pointes must be the same on both lists!");
        for (Point pointOnA : pointsOnA) {
            setPointsNeighbours(pointsOnB, pointOnA);
        }
        for (Point pointOnB : pointsOnB) {
            setPointsNeighbours(pointsOnA, pointOnB);
        }

        for (Point point : pointsOnA) {
            if (checkIfPair(point, point.getNeighbour())) {
                Pair newPair = new Pair(point, point.getNeighbour());
                allPairs.add(newPair);
            }
        }
        return allPairs;
    }

    private void setPointsNeighbours(List<Point> pointsOnB, Point pointOnA) {
        findClosestNeighbour(pointsOnB, pointOnA);
    }

    public double countDistanceFeatures(Point point1, Point point2) {
        double distance = 0;
        for (int i = 0; i < point1.NUMBER_OF_FEATURES; i++) {
            distance += Math.pow((point1.getFeatures()[i] - point2.getFeatures()[i]), 2);
        }
        return Math.sqrt(distance);
    }

    private boolean checkIfPair(Point point1, Point point2) {
        return point1.getNeighbour().equals(point2) && point2.getNeighbour().equals(point1);
    }

    protected static double countDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow((point1.getX() - point2.getX()), 2) + Math.pow((point1.getY() - point2.getY()), 2));
    }

    protected void saveAllPairs(List<Pair> pairs){
        for (Pair pair:pairs){
            allPairs.add(new Pair(pair.getPoint1().copyWithNeighbours(), pair.getPoint2().copyWithNeighbours()));
        }
    }

    protected List<Pair> filterPairsFromModel(List<Pair> allPairs, double maxError){
        List<Pair> filteredPairs = Lists.newArrayList();
        for(Pair pair : allPairs){
            double error = countDistance(pair.getPoint1().getNeighbour(), pair.getPoint2());
            if(error < maxError){
//                LOGGER.info("pair error : {}", error);
                filteredPairs.add(pair);
            }
        }
        return filteredPairs;
    }

}
