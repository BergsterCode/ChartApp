//Author: Dennis Eriksson Berg

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class NamedAlert extends Alert {

    private TextField nameField = new TextField();

    public NamedAlert() {
        super(AlertType.CONFIRMATION);
        GridPane namedGrid = new GridPane();
        namedGrid.addRow(0, new Label("Namn: "), nameField);
        getDialogPane().setContent(namedGrid);
    }

    public String getNamn() {
        return nameField.getText();
    }
}
