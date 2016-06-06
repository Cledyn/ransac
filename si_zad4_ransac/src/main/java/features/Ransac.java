package features;

import com.google.common.collect.Lists;
import model.Pair;
import model.Point;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * Created by Sandra on 2016-06-07.
 */
public class Ransac {


    private static Logger LOGGER = LoggerFactory.getLogger(Ransac.class);
    protected static final int PARAMETER_MATRIX_DIMENSION = 3;
    Random r = new Random();


    protected List<Pair> takeRandomPairs(List<Pair> pairs, int pairsToTake) {
        int pairsNo = pairs.size();
        List<Pair> chosen = Lists.newArrayList();
        for (int i = 0; i < pairsToTake; i++) {
            int index = (int) r.nextDouble() * pairsNo;
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
        RealMatrix matrixInverted = new LUDecomposition(matrix).getSolver().getInverse();
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

}
