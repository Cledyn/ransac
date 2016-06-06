package features;

import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static RealMatrix getAfinicTransformVector(List<Pair> pairs) {
        RealMatrix matrix = prepareAfilicMatrix(pairs);
        RealMatrix vector = prepareVector(pairs);
        return getTransform(matrix, vector);
    }

    public static RealMatrix getAfinicTransformParamsAsMatrix(RealMatrix afinicTransformVector) {
        double[][] afilicParams = new double[PARAMETER_MATRIX_DIMENSION][];
        int counter = 0;
        afilicParams[0] = new double[]{afinicTransformVector.getRow(counter)[0], afinicTransformVector.getRow(counter + 1)[0], afinicTransformVector.getRow(counter + 2)[0]};
        afilicParams[1] = new double[]{afinicTransformVector.getRow(counter + 3)[0], afinicTransformVector.getRow(counter + 4)[0], afinicTransformVector.getRow(counter + 5)[0]};
        afilicParams[2] = new double[]{0, 0, 1};
        return new Array2DRowRealMatrix(afilicParams);
    }

    public static void testForAfinic(){
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
        RealMatrix coord = r.calculateCoordinatesForOtherPic(params, p3);
        LOGGER.info("coord {}", coord.toString());
    }

    public static void main(String[] args) {
        testForAfinic();

    }


}
