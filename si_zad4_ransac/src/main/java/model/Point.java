package model;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;

/**
This is a model class that represents single point on bitmap
 */

public class Point {

    public static final int NUMBER_OF_FEATURES = 128;
    private double x;
    private double y;
    private int[] features;
    private Point neighbour = null;
    private List<Point> neighbourhood;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getPhotoNo() {
        return photoNo;
    }

    private int photoNo;

    public Point(double x, double y, int[] features) {
        this.x = x;
        this.y = y;
        this.features = Arrays.copyOf(features, NUMBER_OF_FEATURES);
        this.neighbour = null;
    }

    public Point(double x, double y, int[] features, int photoNo) {
        this.x = x;
        this.y = y;
        this.features = Arrays.copyOf(features, NUMBER_OF_FEATURES);
//        this.features = new int [NUMBER_OF_FEATURES];
//        System.arraycopy(features, 0,
//                this.features, 0,
//                features.length);
        this.neighbour = null;
        this.photoNo = photoNo;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) return false;
        final Point other = (Point) obj;
        if(this.neighbour == null && other.neighbour !=null){
            return false;
        }
        return checkNeighbourEquality(other);

    }

    private boolean checkNeighbourEquality(Point other) {
        return Objects.equal(this.x, other.x)
                && Objects.equal(this.y, other.y) && (this.neighbour == null && other.neighbour == null ||
                Objects.equal(this.neighbour.getX(),
                        other.neighbour.getX())
                && Objects.equal(this.neighbour.getY(), other.neighbour.getY()));
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(
                this.x, this.y, this.neighbour);

    }

    @Override
    public String toString() {
        return String.valueOf(x) + " " + String.valueOf(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point getNeighbour() {
        return neighbour;
    }

    public void setNeighbour(Point neighbour) {
        this.neighbour = neighbour;
    }

    public int[] getFeatures() {
        return features;
    }

    public void setFeatures(int[] features) {
        this.features = features;
    }

    public List<Point> getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(List<Point> neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
}
