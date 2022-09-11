package cz.fit.ijaproject.controller;

import cz.fit.ijaproject.warehouse.Shelf;
import cz.fit.ijaproject.warehouse.Warehouse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Controller {
    /**
     * Declaration of warehouse, where will be assigned
     * our parsed data or data from user.
     */
    Warehouse warehouse;
    @FXML
    private Button mapStartButton;
    @FXML
    private Scene scene;
    @FXML
    private Stage stage;
    @FXML
    private GridPane mainGrid;

    @FXML
    private TextField selectMap;

    @FXML
    private TextField selectData;

    /**
     * This is the time where needs to be loaded file with map. If it is not,
     * it will remid user that he needs to load atleast file with defined map.
     * @param event reacts to input from user that wants his warehouse to be generated.
     */
    @FXML
    void startmap(ActionEvent event) {
        stage = (Stage) mapStartButton.getScene().getWindow();

        // Object that represents our shelf
        mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(5,5,5,5));
        mainGrid.setVgap(4);
        mainGrid.setHgap(4);
        mainGrid.setMinWidth(80);
        mainGrid.setMinHeight(100);
        mainGrid.setStyle("-fx-background-color:grey");

        warehouse = Warehouse.getInstance();
        ArrayList<Shelf> shelfs = warehouse.getShelves();

        int sizeShelf = shelfs.size();

        int[] x = new int[sizeShelf];
        int[] y = new int[sizeShelf];

        for (int i = 0; i < shelfs.size(); i++) {
            x[i] = shelfs.get(i).getX();
            y[i] = shelfs.get(i).getY();
        }

        boolean shelf = false;

        // This will generate shelfs from input file. If there was any.
        for (int i = 0; i < warehouse.getAreaHeight(); i++) {
                for (int j = 0; j < warehouse.getAreaWidth(); j++) {

                    for (int k = 0; k < sizeShelf; k++) {
                        if (j == x[k] && i == y[k]){
                            shelf = true;
                            break;
                        } else {
                            shelf = false;
                        }
                    }

                    // Normalized map part.
                    Rectangle r = new Rectangle();
                    r.setWidth(98);
                    r.setHeight(52);
                    r.setArcWidth(10);
                    r.setArcHeight(10);

                    if (shelf) {
                        r.setFill(Paint.valueOf("green"));
                        int finalJ = j;
                        int finalI = i;
                        // This action represents creation of shelf.
                        r.setOnMouseClicked(mouseEvent -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(Paths.get("./src/cz/fit/ijaproject/applayout/Shelf.fxml").normalize().toUri().toURL());
                                AnchorPane dialog = loader.load();

                                ShelfDialogController controllerShelf = loader.<ShelfDialogController>getController();
                                controllerShelf.laodDataDialog(finalJ, finalI, r);

                                Scene scened = new Scene(dialog, 400, 320);
                                Stage staged = new Stage();

                                staged.initModality(Modality.APPLICATION_MODAL);
                                staged.setResizable(false);
                                staged.setScene(scened);
                                staged.show();
                            } catch (IOException e) {
                                System.err.println("Chyba pri otváraní okna regálu !");
                            }
                        });
                    } else {
                        r.setFill(Paint.valueOf("grey"));
                        int finalJ = j;
                        int finalI = i;
                        // This action represents deleting shelf from the map.
                        r.setOnMouseClicked(mouseEvent -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(Paths.get("./src/cz/fit/ijaproject/applayout/Shelf.fxml").normalize().toUri().toURL());
                                AnchorPane dialog = loader.load();

                                ShelfDialogController controllerShelf = loader.<ShelfDialogController>getController();
                                controllerShelf.createShelf(finalJ, finalI, r);

                                Scene scened = new Scene(dialog, 400, 320);
                                Stage staged = new Stage();

                                staged.initModality(Modality.APPLICATION_MODAL);
                                staged.setResizable(false);
                                staged.setScene(scened);
                                staged.show();
                            } catch (IOException e) {
                                System.err.println("Chyba pri otváraní okna regálu !");
                            }
                        });
                    }
                    mainGrid.add(r, j, i);
                }
        }

        mainGrid.add(delivery_post(), warehouse.getFinish().x, warehouse.getFinish().y);
        mainGrid.add(cartStart(), warehouse.getStart().x, warehouse.getStart().y);

        scene = new Scene(mainGrid, 1000, 800);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    /**
     * Creating object for delivery post.
     * @return Delivery post.
     */
    Rectangle delivery_post () {
        Rectangle r = new Rectangle();
        r.setWidth(98);
        r.setHeight(52);
        r.setArcWidth(50);
        r.setArcHeight(50);
        r.setFill(Paint.valueOf("red"));
        return r;
    }

    /**
     * Creating delivery cart starting position.
     * @return Starting post.
     */
    Circle cartStart () {
        Circle c = new Circle();
        c.setCenterX(50);
        c.setCenterY(100);
        c.setRadius(25);
        c.setFill(Paint.valueOf("yellow"));
        return c;
    }

}
