//Author: Dennis Eriksson Berg

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DescribedAlert extends Alert {
    private TextField nameField = new TextField();
    private TextField descriptionField = new TextField();

    public DescribedAlert() {
        super(AlertType.CONFIRMATION);
        GridPane describedGrid = new GridPane();
        describedGrid.addRow(0, new Label("Namn: "), nameField);
        describedGrid.addRow(1, new Label("Beskrivning: "), descriptionField);
        getDialogPane().setContent(describedGrid);
    }

    public String getName() {
        return nameField.getText();
    }

    public String getDescription() {
        return descriptionField.getText();
    }
}
