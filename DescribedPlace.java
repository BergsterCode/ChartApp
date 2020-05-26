//Author: Dennis Eriksson Berg
public class DescribedPlace extends Place {

    String description;

    public DescribedPlace(Position xy, String name, String description, String category) {
        super(xy, name, category);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
