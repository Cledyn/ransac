package model;

import com.google.common.base.Objects;

/**
 * This class represents pair of corresponding points on photos
 */
public class Pair {
    private Point point1;
    private Point point2;

    public Pair(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getPoint1() {
        return point1;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) return false;
        final Pair other = (Pair) obj;
        return (Objects.equal(this.point1, other.point1)
                && Objects.equal(this.point2, other.point2)) || (Objects.equal(this.point1, other.point2)
                && Objects.equal(this.point2, other.point1));
    }


//    @Override
//    public int hashCode() {
//
//        return Objects.hashCode(
//                this.x, this.y, this.neighbour);
//
//    }
}
