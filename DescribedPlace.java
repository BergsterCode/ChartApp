//Author: Dennis Eriksson Berg || deer7807@student.su.se
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
