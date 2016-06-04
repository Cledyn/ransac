package heuristic;

/**
 * Created by Sandra on 2016-05-23.
 */
public class RiskRegions implements Heuristic {
    @Override
    public String getName() {
        return "Risk regions";
    }
    public static final int [][] riskRegions = {
            {10, -5,  5,  5,  5,  5, -5, 10},
            {-5, -5, -2, -2, -2, -2, -5,  5},
            { 5, -2,  0,  0,  0,  0, -2, -5},
            { 5, -2,  0,  0,  0,  0, -2,  5},
            { 5, -2,  0,  0,  0,  0, -2,  5},
            { 5, -2,  0,  0,  0,  0, -2,  5},
            {-5, -5, -2, -2, -2, -2, -5, -5},
            {10, -5, 5, 5, 5, 5, -5, 10}
    };
}
