package parser;

import model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sandra on 2016-06-04.
 */
public class FeaturesParser {

    private static final String SPACE_SEPARATOR = " ";

    private void parseFeatures(String fileName){
        Scanner sc = new Scanner(fileName);
        int featuresNo = Integer.parseInt(sc.nextLine());
        int pointsNo = Integer.parseInt(sc.nextLine());
        int [][] features = new int [featuresNo][pointsNo];
        List<Point> points = new ArrayList<Point>(8995);
        int lineIndex = 0;
        while(sc.hasNext()){
            String featuresForSinglePoint = sc.next();
            String [] data = featuresForSinglePoint.split(SPACE_SEPARATOR);
            double x = Double.parseDouble(data[0]);
            lineIndex++;

        }
    }
}
