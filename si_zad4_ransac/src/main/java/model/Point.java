package model;

import com.google.common.base.Objects;

/**
 * Created by Sandra on 2016-06-04.
 */
public class Point {

    private double x;
    private double y;
    private int[] features;
    private Point neighbour = null;

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
}
