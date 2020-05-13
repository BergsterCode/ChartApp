//Author: Dennis Eriksson Berg || deer7807@student.su.se

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class CoordinateButtonAlert extends Alert {

    private TextField xField = new TextField();
    private TextField yField = new TextField();

    public CoordinateButtonAlert() {
        super(AlertType.CONFIRMATION);
        GridPane coordinateGrid = new GridPane();
        coordinateGrid.addRow(0, new Label("x: "), xField);
        coordinateGrid.addRow(1, new Label("y: "), yField);
        getDialogPane().setContent(coordinateGrid);
    }

    public String getXCor() {
        return xField.getText();
    }

    public String getYCor() {
        return yField.getText();
    }
}