package features;

import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.FeaturesParser;
import utils.ImgSizeRetriever;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Sandra on 2016-06-06.
 */
public class RansacAfinic extends Ransac {


    private static Logger LOGGER = LoggerFactory.getLogger(RansacAfinic.class);

    public RansacAfinic() {
        super();
    }

    public static RealMatrix prepareAfilicMatrix(List<Pair> pairs) {
        double[][] firstPointsMatrix = new double[6][];
        for (int i = 0; i < firstPointsMatrix.length; i++) {
            if (i < pairs.size()) {
                firstPointsMatrix[i] = new double[]{pairs.get(i).getPoint1().getX(), pairs.get(i).getPoint1().getY(), 1, 0, 0, 0};
            } else {
                firstPointsMatrix[i] = new double[]{0, 0, 0, pairs.get(i - pairs.size()).getPoint1().getX(), pairs.get(i - pairs.size()).getPoint1().getY(), 1};
            }
        }
        return new Array2DRowRealMatrix(firstPointsMatrix);
    }

    public static RealMatrix getAfinicTransformVector(List<Pair> pairs) { //tu muszą być 3 pary tylko
//        LOGGER.info("List size {}",pairs.size());
        RealMatrix matrix = prepareAfilicMatrix(pairs);
//        LOGGER.info("Matrix size:  rows{}, columns",matrix.getRowDimension(),matrix.getColumnDimension());
        RealMatrix vector = prepareVector(pairs);
//        LOGGER.info("Vector size:  rows{}, columns",vector.getRowDimension(),vector.getColumnDimension());
        return getTransform(matrix, vector);
    }

    public static RealMatrix getAfinicTransformParamsAsMatrix(RealMatrix afinicTransformVector) {
        if (afinicTransformVector == null) {
            return null;
        }
        double[][] afilicParams = new double[PARAMETER_MATRIX_DIMENSION][];
        int counter = 0;
        afilicParams[0] = new double[]{afinicTransformVector.getRow(counter)[0], afinicTransformVector.getRow(counter + 1)[0], afinicTransformVector.getRow(counter + 2)[0]};
        afilicParams[1] = new double[]{afinicTransformVector.getRow(counter + 3)[0], afinicTransformVector.getRow(counter + 4)[0], afinicTransformVector.getRow(counter + 5)[0]};
        afilicParams[2] = new double[]{0, 0, 1};
        return new Array2DRowRealMatrix(afilicParams);
    }

    public RealMatrix ransacAfinic(int iterationNo, List<Pair> pairs, double maxError) {
        {
            RealMatrix bestModel = null;
            RealMatrix model;
            int bestScore = 0;
            boolean modelFound;
            for (int i = 0; i < iterationNo; i++) {
                model = null;
                modelFound = false;
                while (!modelFound) {
                    List<Pair> chosenPairs = takeHeuriticPairs(pairs, 3);
                    model = calculateModel(chosenPairs);
                    if (model != null) {
                        LOGGER.info("found model!");
                        modelFound = true;
                    }
                }
                int score = calculateScore(pairs, model, maxError);
                if (score > bestScore) {
                    bestScore = score;
                    bestModel = model;
                }
            }
            return bestModel;
        }
    }

    private int calculateScore(List<Pair> pairs, RealMatrix model, double maxError) {

        int score = 0;
        for (Pair pair : pairs) {
            double error = modelError(pair, model);
            if (error < maxError) {
                score++;
            }
        }
        return score;
    }


    //wynik tego idzie jako argument do modelerror
    private RealMatrix calculateModel(List<Pair> chosenPairs) {
//        LOGGER.info("Pairs {}", chosenPairs.size());
        RealMatrix vector = RansacAfinic.getAfinicTransformVector(chosenPairs);
        return RansacAfinic.getAfinicTransformParamsAsMatrix(vector);
    }

    //no nie wiem, czy tworzenie nowego obiektu bez sensu jest tu potrzebne...
    private double modelError(Pair pair, RealMatrix model) {
        RealMatrix calculatedPoint2ToPair = calculateCoordinatesForOtherPic(model, pair.getPoint1());
        Point calculated = new Point(calculatedPoint2ToPair.getRow(0)[0], calculatedPoint2ToPair.getRow(1)[0]);
        return countDistance(pair.getPoint2(), calculated);
    }

    private List<Pair> getNewPairsBasedOnModel(RealMatrix bestModel, List<Pair> pairs) {
        List<Pair> newPoints = Lists.newArrayList();
        for (Pair pair : pairs) {
            Point calculated = calculatePoint(bestModel, pair.getPoint1());
            newPoints.add(new Pair(pair.getPoint1(), calculated));
        }
        return newPoints;
    }

    public static void testForAfinic() {
        RansacAfinic r = new RansacAfinic();
        Point p1 = new Point(92.4394, 14.3863);
        Point p2 = new Point(319.087, 137.647);
        Pair pa1 = new Pair(p1, p2);
        Point p3 = new Point(319.143, 49.9722);
        Point p4 = new Point(383.632, 198.621);
        Pair pa2 = new Pair(p3, p4);
        Point p5 = new Point(163.036, 66.7783);
        Point p6 = new Point(153.775, 41.0968);
        Pair pa3 = new Pair(p5, p6);

        List<Pair> list = Lists.newArrayList();
        list.add(pa1);
        list.add(pa2);
        list.add(pa3);
        RealMatrix vector = RansacAfinic.getAfinicTransformVector(list);
        LOGGER.info("Vector {}", vector.toString());
        RealMatrix params = RansacAfinic.getAfinicTransformParamsAsMatrix(vector);
        LOGGER.info("Params {}", params.toString());
        RealMatrix coord1 = r.calculateCoordinatesForOtherPic(params, p1);
        LOGGER.info("coord1 {}", coord1.toString());
        RealMatrix coord2 = r.calculateCoordinatesForOtherPic(params, p3);
        LOGGER.info("coord2 {}", coord2.toString());
        RealMatrix coord3 = r.calculateCoordinatesForOtherPic(params, p5);
        LOGGER.info("coord3 {}", coord3.toString());
    }

    public List<Pair> run(String file1, String file2, int iterationNo, double maxError) throws FileNotFoundException {
        Long time = System.currentTimeMillis();
        List<Point> pointsOnA = FeaturesParser.parseFeatures(ImgSizeRetriever.class.getClassLoader().getResource(file1).getFile(), 0);
        List<Point> pointsOnB = FeaturesParser.parseFeatures(ImgSizeRetriever.class.getClassLoader().getResource(file2).getFile(), 1);
        List<Pair> pairs = makePairs(pointsOnA, pointsOnB);
        saveAllPairs(pairs);

        LOGGER.info("pairs : {}", pairs.size());
        RealMatrix bestModel = ransacAfinic(iterationNo, pairs, maxError);
        if (bestModel != null) {
            LOGGER.info("Success!");
        }
        List<Pair> pairsBasedOnModel = getNewPairsBasedOnModel(bestModel, pairs);
        LOGGER.info("pairs based on model : {}", pairsBasedOnModel.size());
        List<Pair> filteredPairs = filterPairsFromModel(pairsBasedOnModel, maxError);
        LOGGER.info("pairs filtered : {}", filteredPairs.size());
        time = System.currentTimeMillis() - time;
        LOGGER.info("TIME of ransac: {}", time);
        return filteredPairs;
    }

    public static void main(String[] args) throws FileNotFoundException {
//        testForAfinic();
        new RansacAfinic().run("d1.png.haraff.sift", "d2.png.haraff.sift",2,1);
    }


}
