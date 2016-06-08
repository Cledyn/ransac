package model;

import java.io.File;
import java.util.List;


public class Photo {
    private File filePath;
    private List<Point> points;
    private List<Pair> pairs;
    private List<Pair> filtered_pairs;

    public Photo(File filePath, List<Point> points) {
        this.filePath = filePath;
        this.points = points;
    }

    public Photo(File filePath, List<Point> points, List<Pair> pairs, List<Pair> filtered_pairs) {
        this(filePath, points);
        this.pairs = pairs;
        this.filtered_pairs = filtered_pairs;
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

    public List<Pair> getFiltered_pairs() {
        return filtered_pairs;
    }
}
