//Author: Dennis Eriksson Berg || deer7807@student.su.se

public class Position {

    private double x;
    private double y;
    private Place place;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPlace(Place p) {
        place = p;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position position = (Position) o;
            if (x == position.x && y == position.y) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (int) (x * 10000 + y);
    }
}

