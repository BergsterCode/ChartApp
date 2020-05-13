//Author: Dennis Eriksson Berg || deer7807@student.su.se

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main extends Application {
    //Datastrukturer
    Map<Position, Place> positionPlaceMap = new HashMap<>();
    Map<String, ArrayList<Place>> placeCategoryMap = new HashMap<>();
    Map<String, ArrayList<Place>> placeNameMap = new HashMap<>();
    ArrayList<Place> markedPlaceList = new ArrayList<>();

    //Globala Variabler
    private Pane panel;
    private VBox vBoxRootTop;
    private ImageView bg;
    private ImageView bg1;
    private Image image;
    private Button ny;
    private Button hideCategory;
    private RadioButton named;
    private RadioButton described;
    private TextField searchField;
    private Button searchButton;
    private Button hideButton;
    private Button removeButton;
    private Button coordinateButton;
    private ClickHandler clickHandler = new ClickHandler();
    private boolean changeFlag = false;
    private boolean saveFlag = false;
    private ListView listView;
    private boolean mapLoaded = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Inlupp 2. Author: Dennis Eriksson Berg, Deer7807@student.su.se");
        BorderPane root = new BorderPane();
        panel = new Pane();
        root.setCenter(panel);
        vBoxRootTop = new VBox();
        root.setTop(vBoxRootTop);


        //lägger till menybar
        MenuBar menuBar = new MenuBar();
        VBox vBoxTop = new VBox(menuBar);
        //Lägger till meny
        Menu meny1 = new Menu("File");
        menuBar.getMenus().add(meny1);
        //Lägger till menyitems dropdown
        MenuItem loadMap = new MenuItem("New Map");
        MenuItem loadPlaces = new MenuItem("Load Places");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new ExitHandler());
        meny1.getItems().addAll(loadMap, loadPlaces, save, exit);

        //Action event för att ladda in en karta.
        loadMap.setOnAction(event -> {
            if (changeFlag == false) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose your map");
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    image = new Image(file.toURI().toString());
                    bg = new ImageView(image);
                    panel.getChildren().add(bg);
                    mapLoaded = true;
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("OBS! Det finns osparade ändringar. Vill ni ladda ny karta ändå?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
                if (res.isPresent() && res.get().equals(ButtonType.OK)) {
                    deletsPlaces();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose your map");
                    File fil = fileChooser.showOpenDialog(primaryStage);
                    if (fil != null) {
                        if(bg != null){
                            panel.getChildren().remove(bg);
                        }
                        image = new Image(fil.toURI().toString());
                        bg1 = new ImageView(image);
                        panel.getChildren().add(bg1);
                        mapLoaded = true;
                    }
                }
            }
        });

        //Action event för att ladda in platser
        loadPlaces.setOnAction(event -> {
            if (mapLoaded == true) {
                if (changeFlag == false) {
                    try {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Choose file containing Places");
                        File file = fileChooser.showOpenDialog(primaryStage);

                        if (file != null) {
                            FileReader in = new FileReader(file);
                            BufferedReader br = new BufferedReader(in);
                            String line;
                            while ((line = br.readLine()) != null) {
                                String[] tokens = line.split(",");
                                String placeType = tokens[0];
                                String category = tokens[1].replace("\n", "").replace("\r", "");
                                double x = Double.parseDouble(tokens[2]);
                                double y = Double.parseDouble(tokens[3]);
                                String name = tokens[4];
                                Position p = new Position(x, y);
                                if (placeType.equals("Named")) {
                                    NamedPlace namedPlace = new NamedPlace(p, name, category);
                                    addNewPlace(namedPlace);
                                    panel.getChildren().add(namedPlace);
                                } else {
                                    String description = tokens[5];
                                    DescribedPlace describedPlace = new DescribedPlace(p, name, description, category);
                                    addNewPlace(describedPlace);
                                    panel.getChildren().add(describedPlace);
                                }
                            }
                            br.close();
                            in.close();
                            changeFlag = true;
                        }


                    } catch (FileNotFoundException e) {
                        new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
                    } catch (IOException e) {
                        new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
                    }

                } else {
                    try {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setContentText("Obs! Det finns osparade ändringar, vill ni ladda nya platser ändå?");
                        Optional<ButtonType> res = alert.showAndWait();
                        if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                            event.consume();
                        }
                        if (res.isPresent() && res.get().equals(ButtonType.OK)) {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Choose file containing Places");
                            File file = fileChooser.showOpenDialog(primaryStage);
                            if (file != null) {
                                deletsPlaces();
                                FileReader in = new FileReader(file);
                                BufferedReader br = new BufferedReader(in);
                                String line;
                                while ((line = br.readLine()) != null) {
                                    String[] tokens = line.split(",");
                                    String placeType = tokens[0];
                                    String category = tokens[1].replace("\n", "").replace("\r", "");
                                    double x = Double.parseDouble(tokens[2]);
                                    double y = Double.parseDouble(tokens[3]);
                                    String name = tokens[4];
                                    Position p = new Position(x, y);
                                    if (placeType.equals("Named")) {
                                        NamedPlace namedPlace = new NamedPlace(p, name, category);
                                        addNewPlace(namedPlace);
                                        panel.getChildren().add(namedPlace);
                                    } else {
                                        String description = tokens[5];
                                        DescribedPlace describedPlace = new DescribedPlace(p, name, description, category);
                                        addNewPlace(describedPlace);
                                        panel.getChildren().add(describedPlace);
                                    }
                                }
                                br.close();
                                in.close();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
                    } catch (IOException e) {
                        new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
                    }
                }
            }

        });

        save.setOnAction(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Spara");
                File file = fileChooser.showSaveDialog(primaryStage);
                FileWriter utfil = new FileWriter(file);
                PrintWriter out = new PrintWriter(utfil);
                for (Place p : positionPlaceMap.values()) {
                    if (p instanceof DescribedPlace) {
                        DescribedPlace d = (DescribedPlace) p;
                        out.println("Described" + "," + d.getCategory() + "," + d.getNewPosition().getX()
                                + "," + d.getNewPosition().getY() + "," + d.getName() + "," + d.getDescription());
                    } else {
                        out.println("Named," + p.getCategory() + "," + p.getNewPosition().getX() +
                                "," + p.getNewPosition().getY() + "," + p.getName());
                    }
                    System.out.println("Saved");
                }
                utfil.close();
                out.close();
                changeFlag = false;
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Fel!").showAndWait();
            }
        });

        HBox hBoxTop = new HBox();
        hBoxTop.setPadding(new Insets(5));
        hBoxTop.setSpacing(5);
        hBoxTop.setAlignment(Pos.CENTER);
        ny = new Button("New");

        ny.setOnAction(new NyButtonHandler());

        VBox radioVBoxTop = new VBox();
        named = new RadioButton("Named");
        described = new RadioButton("Described");

        named.setSelected(true);
        ToggleGroup radio = new ToggleGroup();
        named.setToggleGroup(radio);
        described.setToggleGroup(radio);

        radioVBoxTop.getChildren().addAll(named, described);
        searchField = new TextField("Search");
        searchButton = new Button("Search");
        searchButton.setOnAction(new SearchHandler());
        hideButton = new Button("Hide");
        hideButton.setOnAction(new HideButtonHandler());
        removeButton = new Button("Remove");
        removeButton.setOnAction(new RemoveButtonHandler());
        coordinateButton = new Button("Coordinates");
        coordinateButton.setOnAction(new CoordinateButtonHandler());
        hBoxTop.getChildren().addAll(ny, radioVBoxTop, searchField, searchButton, hideButton, removeButton, coordinateButton);
        vBoxRootTop.getChildren().addAll(vBoxTop, hBoxTop);

        //FlowPane för högersidan
        VBox right = new VBox();
        right.setPadding(new Insets(10));
        right.setSpacing(5);
        right.setAlignment(Pos.CENTER);
        root.setRight(right);



        //Lista med alternativ som kan väljas i ListView
        ObservableList<String> menyVal = FXCollections.observableArrayList("Bus", "Underground", "Train");
        //ListView för kategorierna
        listView = new ListView(menyVal);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setMaxSize(140, 80);
        listView.getSelectionModel().selectedItemProperty().addListener(new ListHandler());

        //knapp för HideCategory
        hideCategory = new Button("Hide Category");
        hideCategory.setOnAction(new HideCategoryButtonHandler());
        right.getChildren().addAll(new Label("Category"), listView, hideCategory);


        placeCategoryMap.put("Bus", new ArrayList<Place>());
        placeCategoryMap.put("None", new ArrayList<Place>());
        placeCategoryMap.put("Underground", new ArrayList<Place>());
        placeCategoryMap.put("Train", new ArrayList<Place>());

        //Sätter upp scenen (För att programmet ska funka).
        primaryStage.setOnHiding(windowEvent -> {
            if (changeFlag == true) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Osparade ändringar. Avsluta ändå?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    windowEvent.consume();
                if (res.isPresent() && res.get().equals(ButtonType.OK)) {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });

        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void newPlace(Position pos) {
        String name = null;
        String description = null;
        Place newPlace = null;
        String category = (String) listView.getSelectionModel().getSelectedItem();
        if (category == null) {
            category = "None";
        }

        if (named.isSelected()) {
            try {
                NamedAlert dialog = new NamedAlert();
                dialog.setHeaderText("Registrera plats");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    name = dialog.getNamn();
                    if (name.trim().isEmpty()) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Tomt namn!");
                        msg.showAndWait();
                        return;
                    }
                    newPlace = new NamedPlace(pos, name, category);
                    panel.getChildren().add(newPlace);
                }

            } catch (Exception e) {
                System.out.println("Hejzan");
                Alert exception = new Alert(Alert.AlertType.ERROR);
                exception.setContentText(e.getMessage());
                exception.showAndWait();
            }
        }

        if (described.isSelected()) {
            try {
                DescribedAlert dialog = new DescribedAlert();
                dialog.setHeaderText("Registrera plats & beskrivning");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    name = dialog.getName();
                    if (name.trim().isEmpty()) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Tomt namn!");
                        msg.showAndWait();
                        return;
                    }
                    description = dialog.getDescription();
                    if (description.trim().isEmpty()) {
                        Alert msg1 = new Alert(Alert.AlertType.ERROR, "Tom Beskrivning");
                        msg1.showAndWait();
                        return;
                    }
                    newPlace = new DescribedPlace(pos, name, description, category);
                    panel.getChildren().add(newPlace);
                }

            } catch (Exception e) {
                Alert exception = new Alert(Alert.AlertType.ERROR);
                exception.setContentText(e.getMessage());
                exception.showAndWait();
            }
        }
        if (newPlace != null) {
            addNewPlace(newPlace);
            changeFlag = true;
        }
    }

    private void addNewPlace(Place place) {

        if (place != null) {

            if (!positionPlaceMap.equals(place)) {
                place.setOnMouseClicked(new ClickTriangle());
                placeCategoryMap.get(place.getCategory()).add(place);
                positionPlaceMap.put(place.getNewPosition(), place);

                if (placeNameMap.containsKey(place.getName())) {
                    placeNameMap.get(place.getName()).add(place);
                } else {
                    ArrayList<Place> list = new ArrayList<>();
                    placeNameMap.put(place.getName(), list);
                    placeNameMap.get(place.getName()).add(place);
                }

            } else {
                Alert msg = new Alert(Alert.AlertType.ERROR, "Platsen finns redan!");
                msg.showAndWait();
            }
        }
    }

    private void deletsPlaces() {
        ArrayList<Place> temp = new ArrayList<>(positionPlaceMap.values());
        for (Place p : temp) {
            removeEverything(p);
        }
    }

    private void removeEverything(Place p) {
        removeMarkedStateAll();
        p.setMarked(false);
        p.setVisible(false);
        String name = p.getName();
        String category = p.getCategory();
        Position position = p.getNewPosition();
        positionPlaceMap.remove(position, p);
        placeCategoryMap.get(category).remove(position);
        placeNameMap.get(name).remove(p);
        if (placeNameMap.get(name).isEmpty()) {
            ArrayList<Place> temp = placeNameMap.get(name);
            placeNameMap.remove(name, temp);
        }
    }

    class NyButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (mapLoaded == true) {
                panel.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
                panel.setCursor(Cursor.CROSSHAIR);
                ny.setDisable(true);
            }
        }
    }


    private void removeMarkedStateAll() {
        for (Place place : markedPlaceList) {
            place.setMarked(false);
        }
        markedPlaceList.clear();
    }

    private void removePlaces(Place place) {
        String name = place.getName();
        String category = place.getCategory();
        Position position = place.getNewPosition();
        if (place.getMarked() == true) {
            positionPlaceMap.remove(position, place);
            placeCategoryMap.remove(category, place);
            placeNameMap.remove(name, place);
            panel.getChildren().remove(place);
            place.setMarked(false);
            changeFlag = true;
        }
    }

    class RemoveButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for (Place place : markedPlaceList) {
                removePlaces(place);
            }
            markedPlaceList.clear();
        }
    }

    private void hidePlaces(Place place) {
        if (place.getMarked() == true) {
            place.setVisible(false);
        }
    }

    class HideButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            for (Place place : markedPlaceList) {
                hidePlaces(place);
            }
        }
    }

    class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();

            System.out.println("Skapar nyPos");
            Position nyPos = new Position(x, y);
            //Läggs i slutet
            panel.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            panel.setCursor(Cursor.DEFAULT);
            ny.setDisable(false);
            //Skicka position till newPlace
            newPlace(nyPos);
            //Renssa listView efter val har gjorts.
            listView.getSelectionModel().clearSelection();
        }
    }

    class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changeFlag == true) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Osparade ändringar. Avsluta ändå?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL))
                    event.consume();
                if (res.isPresent() && res.get().equals(ButtonType.OK)) {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }
    }

    class ClickTriangle implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Place place = (Place) event.getSource();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!place.getMarked()) {
                    place.setMarked(true);
                    markedPlaceList.add(place);
                    System.out.println("Platsen är markerad" + place.getNewPosition().getX() + "[]" + place.getNewPosition().getY());
                } else {
                    markedPlaceList.remove(place);
                    place.setMarked(false);
                    System.out.println("platsen är inte längre markerad");
                }
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                if (place instanceof NamedPlace) {
                    Alert message = new Alert(Alert.AlertType.INFORMATION, place.getName() + " [" + place.getNewPosition().getX() + " ," + place.getNewPosition().getY() + "]");
                    message.showAndWait();
                }
                if (place instanceof DescribedPlace) {
                    DescribedPlace describedPlace = (DescribedPlace) place;
                    Alert message1 = new Alert(Alert.AlertType.INFORMATION, place.getName() + " [" + place.getNewPosition().getX() + " ," + place.getNewPosition().getY() + "]" + "\n" + describedPlace.getDescription());
                    message1.showAndWait();
                }
            }
        }
    }

    class HideCategoryButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            removeMarkedStateAll();
            String category = (String) listView.getSelectionModel().getSelectedItem();

            if (placeCategoryMap.get(category) != null) {
                ArrayList<Place> temp = placeCategoryMap.get(category);
                for (Place p : temp) {
                    markedPlaceList.add(p);
                    p.setVisible(false);
                }
            }
            listView.getSelectionModel().clearSelection();
        }
    }

    class ListHandler implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue obs, String old, String nev) {
            System.out.println(old + " changed to " + nev);
            if (placeCategoryMap.get(nev) != null) {
                ArrayList<Place> temp = placeCategoryMap.get(nev);
                for (Place p : temp) {
                    p.setVisible(true);
                }
            }
        }
    }

    class CoordinateButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                CoordinateButtonAlert dialog = new CoordinateButtonAlert();
                dialog.setHeaderText("Sök koordinater");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    String x = dialog.getXCor();
                    Double xd = Double.parseDouble(x);
                    System.out.println("xd: " + xd);
                    if (xd < 0) {
                        Alert msg = new Alert(Alert.AlertType.ERROR, "Ogiltig inmatning!");
                        msg.showAndWait();
                        return;
                    }
                    String y = dialog.getYCor();
                    Double yd = Double.parseDouble(y);
                    System.out.println("yd: " + yd);
                    if (yd < 0) {
                        Alert msg1 = new Alert(Alert.AlertType.ERROR, "Ogiltig inmatning");
                        msg1.showAndWait();
                        return;
                    }
                    Position position = new Position(xd, yd);

                    if (positionPlaceMap.containsKey(position)) {
                        System.out.println("inne i if satsen");
                        Place place = positionPlaceMap.get(position);
                        removeMarkedStateAll();
                        place.setMarked(true);
                        place.setVisible(true);
                        markedPlaceList.add(place);
                    } else {
                        Alert msg2 = new Alert(Alert.AlertType.ERROR, "Platsen finns inte i registret!");
                        msg2.showAndWait();
                        return;
                    }
                }

            } catch (Exception e) {
                Alert exception = new Alert(Alert.AlertType.ERROR);
                exception.setContentText(e.getMessage());
                exception.showAndWait();
            }
        }
    }

    class SearchHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (searchField.getText() != "Search" && searchField.getText() != null) {
                removeMarkedStateAll();
                String text = searchField.getText();
                System.out.println(Collections.singletonList(placeNameMap));
                if (placeNameMap.keySet().contains(text)) {
                    System.out.println("texten finns i placenamemap");
                    ArrayList<Place> temp = placeNameMap.get(text);
                    for (Place place : temp) {
                        markedPlaceList.add(place);
                        place.setVisible(true);
                        place.setMarked(true);
                    }

                } else {
                    new Alert(Alert.AlertType.ERROR, "Ingen plats finns med det angivna namnet");
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Fel!");
            }

        }
    }

    //Main metod
    public static void main(String[] args) {
        launch(args);
    }

}