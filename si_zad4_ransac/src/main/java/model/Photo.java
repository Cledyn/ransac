package model;

import java.io.File;
import java.util.List;

/**
 * Created by Martyna on 05.06.2016.
 */
public class Photo {
    private File filePath;
    private List<Point> points;
    private List<Pair> pairs;

    public Photo(File filePath, List<Point> points) {
        this.filePath = filePath;
        this.points = points;
    }

    public Photo(File filePath, List<Point> points, List<Pair> pairs) {
        this(filePath, points);
        this.pairs = pairs;
    }

    public File getFilePath() {
        return filePath;
    }

    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }
}
