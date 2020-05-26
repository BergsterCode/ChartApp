//Author: Dennis Eriksson Berg

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class Place extends Polygon {

    private Position newPosition;
    private String category;
    private String name;
    private boolean isMarked = false;

    public Place(Position xy, String name, String category) {
        newPosition = xy;
        this.name = name;
        this.category = category;
        xy.setPlace(this);
        setColor(category);
        getPoints().setAll(newPosition.getX(), newPosition.getY(), newPosition.getX() - 15, newPosition.getY() - 30, newPosition.getX() + 15, newPosition.getY() - 30);
        relocate(newPosition.getX() - 15, newPosition.getY() - 30);
    }

    protected void setColor(String category) {
        if (category.equals("Bus")) {
            setFill(Color.RED);
        }
        if (category.equals("Underground")) {
            setFill(Color.BLUE);
        }
        if (category.equals("Train")) {
            setFill(Color.GREEN);
        }
        if (category.equals("None")) {
            setFill(Color.BLACK);
        }
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setMarked(boolean b) {
        isMarked = b;
    }

    public boolean getMarked() {
        return isMarked;
    }

    //Skapa funktionalitet på hashtable

    public Position getNewPosition() {
        return newPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Place) {
            Place place = (Place) obj;
            return newPosition.equals(place.getNewPosition());
        }
        return false;
    }

    public int hashCode() {
        int x = (int) newPosition.getX();
        int y = (int) newPosition.getY();
        return x * 10000 + y;
    }
}
