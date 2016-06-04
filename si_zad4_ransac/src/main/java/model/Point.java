package model;

import com.google.common.base.Objects;

/**
 * Created by Sandra on 2016-06-04.
 */
public class Point {

    public static final int NUMBER_OF_FEATIRES = 128;
    private double x;
    private double y;
    private int[] features;
    private Point neighbour = null;

    public Point(double x, double y, int[] features) {
        this.x = x;
        this.y = y;
        System.arraycopy(features, 0, this.features, 0, features.length);
        this.neighbour = null;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) return false;
        final Point other = (Point) obj;
        return Objects.equal(this.x, other.x)
                && Objects.equal(this.y, other.y)
                && Objects.equal(this.neighbour, other.neighbour);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(
                this.x, this.y, this.neighbour);

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
}
