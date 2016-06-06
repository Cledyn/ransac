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
public class RansacPerspective extends Ransac {


    private static Logger LOGGER = LoggerFactory.getLogger(RansacPerspective.class);

    public RansacPerspective() {
        super();
    }

    public static RealMatrix preparePerspectiveMatrix(List<Pair> pairs) {
        double[][] firstPointsMatrix = new double[pairs.size() * 2][];
        for (int i = 0; i < firstPointsMatrix.length; i++) {
            if (i < pairs.size()) {
                firstPointsMatrix[i] = new double[]{pairs.get(i).getPoint1().getX(), pairs.get(i).getPoint1().getY(), 1, 0, 0, 0, -pairs.get(i).getPoint2().getX() * pairs.get(i).getPoint1().getX(), -pairs.get(i).getPoint2().getY() * pairs.get(i).getPoint1().getY()};
            } else {
                int currIndex = i - pairs.size();
                firstPointsMatrix[i] = new double[]{0, 0, 0, pairs.get(currIndex).getPoint1().getX(), pairs.get(currIndex).getPoint1().getY(), 1, -pairs.get(currIndex).getPoint2().getY() * pairs.get(currIndex).getPoint1().getX(), -pairs.get(currIndex).getPoint2().getY() * pairs.get(currIndex).getPoint1().getX()};
            }
        }
        return new Array2DRowRealMatrix(firstPointsMatrix);
    }

    public static RealMatrix getPerspectiveTransformVector(List<Pair> pairs) {
        RealMatrix matrix = preparePerspectiveMatrix(pairs);
        RealMatrix vector = prepareVector(pairs);
        return getTransform(matrix, vector);
    }

    public static RealMatrix getPerspectiveTransformParamsAsMatrix(RealMatrix perspectiveTransformVector) {
        double[][] perspectiveParams = new double[PARAMETER_MATRIX_DIMENSION][];
        int counter = 0;
        perspectiveParams[0] = new double[]{perspectiveTransformVector.getRow(counter)[0], perspectiveTransformVector.getRow(counter + 1)[0], perspectiveTransformVector.getRow(counter + 2)[0]};
        perspectiveParams[1] = new double[]{perspectiveTransformVector.getRow(counter + 3)[0], perspectiveTransformVector.getRow(counter + 4)[0], perspectiveTransformVector.getRow(counter + 5)[0]};
        perspectiveParams[2] = new double[]{perspectiveTransformVector.getRow(counter + 6)[0], perspectiveTransformVector.getRow(counter + 7)[0], 1};
        return new Array2DRowRealMatrix(perspectiveParams);
    }

    public static void testForPerspective(){

        RansacPerspective r = new RansacPerspective();
        Point p1 = new Point(92.4394, 14.3863);
        Point p2 = new Point(319.087, 137.647);
        Pair pa1 = new Pair(p1, p2);
        Point p3 = new Point(319.143, 49.9722);
        Point p4 = new Point(383.632, 198.621);
        Pair pa2 = new Pair(p3, p4);
        Point p5 = new Point(163.036, 66.7783);
        Point p6 = new Point(153.775, 41.0968);
        Pair pa3 = new Pair(p5, p6);
        Point p7 = new Point(26.7973, 143.959);
        Point p8 = new Point(210.369, 239.478);
        Pair pa4 = new Pair(p7, p8);
        List<Pair> list = Lists.newArrayList();
        list.add(pa1);
        list.add(pa2);
        list.add(pa3);
        list.add(pa4);
        RealMatrix vector = RansacPerspective.getPerspectiveTransformVector(list);
        LOGGER.info("Vector {}", vector.toString());
        RealMatrix params = RansacPerspective.getPerspectiveTransformParamsAsMatrix(vector);
        LOGGER.info("Params {}", params.toString());
        RealMatrix coord = r.calculateCoordinatesForOtherPic(params, p3);
        LOGGER.info("coord {}", coord.toString());
    }

    public static void main(String[] args) {
        testForPerspective();
    }


}
