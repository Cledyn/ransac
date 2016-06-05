package parser;

import model.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sandra on 2016-06-04.
 */
public final class FeaturesParser {

    private static Logger LOGGER = LoggerFactory.getLogger(FeaturesParser.class);
    private static final String SPACE_SEPARATOR = " ";
    private static final int X_COORDINATE_INDEX_IN_LINE = 0;
    private static final int Y_COORDINATE_INDEX_IN_LINE = 1;
    private static final int FEATURE_START_INDEX = 5;

    public static List<Point> parseFeatures(String fileName, int photoNo) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        int featuresNo = Integer.parseInt(sc.nextLine());
        int pointsNo = Integer.parseInt(sc.nextLine());
        List<Point> points = new ArrayList<Point>(pointsNo);
        while (sc.hasNext()) {
            String featuresForSinglePoint = sc.nextLine();
            Point nextPoint = readDataForSinglePoint(featuresForSinglePoint, photoNo);
            points.add(nextPoint);

        }
        sc.close();
        return points;
    }

    private static Point readDataForSinglePoint(String featuresForSinglePoint, int photoNo) {
        String[] data = featuresForSinglePoint.split(SPACE_SEPARATOR);
        double x = Double.parseDouble(data[X_COORDINATE_INDEX_IN_LINE]);
        double y = Double.parseDouble(data[Y_COORDINATE_INDEX_IN_LINE]);
        int[] featureTmpArr = getFeaturesOnlyForPoint(data);
        return new Point(x, y, featureTmpArr, photoNo);
    }

    private static int[] getFeaturesOnlyForPoint(String[] data) {
        int[] featureTmpArr = new int[data.length - FEATURE_START_INDEX + 1];
        int featureCounter = 0;
        for (int i = FEATURE_START_INDEX; i < data.length; i++) {
            featureTmpArr[featureCounter++] = Integer.parseInt(data[i]);
        }
        return featureTmpArr;
    }

}
