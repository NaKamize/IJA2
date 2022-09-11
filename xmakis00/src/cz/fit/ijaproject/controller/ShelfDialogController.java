package cz.fit.ijaproject.controller;

import cz.fit.ijaproject.warehouse.Category;
import cz.fit.ijaproject.warehouse.Shelf;
import cz.fit.ijaproject.warehouse.Type;
import cz.fit.ijaproject.warehouse.Warehouse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ShelfDialogController {
    /**
     * Declaration of the controls that
     * communicate with user.
     */
    @FXML
    private TextField regalIdField;

    @FXML
    private TextField typeIdField;

    @FXML
    private TextField countField;

    @FXML
    private TextField nameTypeField;

    @FXML
    private TextField weightTypeField;

    @FXML
    private TextField categoryField;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField categoryIdField;
    /**
     * Declaration of warehouse, where will be assigned
     * our parsed data or data from user.
     */
    Warehouse warehouse;
    /**
     * Certain data of the shelf whitch was
     * opened by user. Those data could be
     * edited by user. But not everything could be
     * because IDs could not be edited.
     */
    private int shelfId;
    private int shelfCount;
    private int shelfTypeId;
    private double shelfWeight;
    private String shelfTypeName;
    private int shelfCategoryId;
    private String shelfCategoryName;
    private Rectangle rectangle;
    private ArrayList<Shelf> shelfs;
    private int x;
    private int y;
    /**
     * Loding another scene for certain shelf which was selected by a user.
     * @param x X coordinates.
     * @param y Y coordinates.
     * @param rectangle Rectangle that represents shelf.
     */
    public void laodDataDialog (int x, int y, Rectangle rectangle) {
        warehouse = Warehouse.getInstance();
        shelfs = warehouse.getShelves();
        // This finds our shelf, that was selected by user.
        for (Shelf shelf : shelfs) {
            if (shelf.getX() == x && shelf.getY() == y) {
                this.x = shelf.getX();
                this.y = shelf.getY();
                this.shelfId = shelf.getId();
                this.shelfCount = shelf.getCount();
                this.shelfTypeId = shelf.getType().getId();
                this.shelfWeight = shelf.getType().getWeight();
                this.shelfTypeName = shelf.getType().getName();
                this.shelfCategoryId = shelf.getType().getCategory().getId();
                this.shelfCategoryName = shelf.getType().getCategory().getName();
                this.rectangle = rectangle;
            }
        }

        setUpTextFields();
    }
    /**
     * This sets up text fileds with data loaded from input file.
     * And shows them to a user.
     */
    private void setUpTextFields () {
        regalIdField.setText(String.valueOf(shelfId));
        regalIdField.setEditable(false);

        typeIdField.setText(String.valueOf(shelfTypeId));
        typeIdField.setEditable(false);

        categoryIdField.setText(String.valueOf(shelfCategoryId));
        categoryIdField.setEditable(false);

        categoryField.setText(String.valueOf(shelfCategoryName));
        weightTypeField.setText(String.valueOf(shelfWeight));
        nameTypeField.setText(shelfTypeName);
        countField.setText(String.valueOf(shelfCount));
        typeIdField.setText(String.valueOf(shelfTypeId));
        categoryIdField.setText(String.valueOf(shelfCategoryId));

        ConfirmButton("OK");
    }
    /**
     * Sets text to a button that creates new shelf.
     * @param text Text for button in creation process.
     */
    private void ConfirmButton (String text) {
        addButton.setText(text);
    }
    /**
     * Methos for confirmation is the same as addition.
     * Diffence bethween confirmation and addition is
     * condition that checks wether shelf on this
     * coordinates was created or not.
     * @param event Addition a confirmation event.
     */
    @FXML
    void AddOrConfirmShelf(ActionEvent event) {
        try {
            shelfs.add(new Shelf(shelfId, x, y,
                new Type(shelfTypeId, shelfWeight, shelfTypeName,
                        new Category(shelfCategoryId, shelfCategoryName)), shelfCount));

            warehouse.setShelves(shelfs);
            System.out.println(shelfs);
            rectangle.setFill(Paint.valueOf("green"));
        } catch (Exception e) {
            System.err.println("Regál sa nepodarilo pridať !");
        }

    }
    /**
     * Method that represents shelf remove.
     * @param event Remove shelf.
     */
    @FXML
    void removeShelf(ActionEvent event) {
        try {
            int shelfSize = shelfs.size();
            for (int i = 0; i < shelfSize; i++) {
                if (shelfs.get(i).getX() == this.x && shelfs.get(i).getY() == this.y) {
                    shelfs.remove(i);
                    break;
                }
            }
            warehouse.setShelves(shelfs);
            rectangle.setFill(Paint.valueOf("grey"));
        } catch (Exception e) {
            System.err.println("Regál nemožno odstrániť !");
        }
    }
    /**
     * Method that represents creation of new shelf.
     * @param x X coordinates.
     * @param y Y coordinates.
     * @param rectangle Object that is shown on the map.
     */
    void createShelf (int x, int y, Rectangle rectangle) {
        this.x = x;
        this.y = y;
        this.rectangle = rectangle;
        warehouse = Warehouse.getInstance();
        shelfs = warehouse.getShelves();

        ConfirmButton("Pridať");
    }
    /**
     * Method that parses loaded data
     * from textlabel and stores them into
     * shelfs list.
     */
    void parseFieldData () {

    }
    /**
     * Entered and confimed new data into Category label.
     * @param event Entered data.
     */
    @FXML
    void categoryIdAction (ActionEvent event) {
        if (categoryIdField.getText().equals("")) {
            return;
        }

        shelfCategoryId = Integer.parseInt(categoryIdField.getText());
    }
    /**
     * User left the data in text label, but their are still
     * used as a category.
     * @param event Left data in text label.
     */
    @FXML
    void categoryIdExited (MouseEvent event) {
        if (categoryIdField.getText().equals("")) {
            return;
        }
        shelfCategoryId = Integer.parseInt(categoryIdField.getText());
    }
    /**
     * Shelf id detection.
     * @param event Left data in text label.
     */
    @FXML
    void shelfIdExited (MouseEvent event) {
        if (regalIdField.getText().equals("")) {
            return;
        }
        shelfId = Integer.parseInt(regalIdField.getText());
    }
    /**
     * Shelf id detection.
     * @param event Entered data.
     */
    @FXML
    void shelfIdAction (ActionEvent event) {
        if (regalIdField.getText().equals("")) {
            return;
        }
        shelfId = Integer.parseInt(regalIdField.getText());
    }
    /**
     * Type id detection.
     * @param event Entered data.
     */
    @FXML
    void typeIdAction (ActionEvent event) {
        if (typeIdField.getText().equals("")) {
            return;
        }
        shelfTypeId = Integer.parseInt(typeIdField.getText());
    }
    /**
     * Type id detection.
     * @param event Left data in text label.
     */
    @FXML
    void typeIdExited (MouseEvent event) {
        if (typeIdField.getText().equals("")) {
            return;
        }
        shelfTypeId = Integer.parseInt(typeIdField.getText());
    }
    /**
     * Cathegory name detection.
     * @param event Entered data.
     */
    @FXML
    void categoryNameAction (ActionEvent event) {
        if (categoryField.getText().equals("")) {
            return;
        }
        shelfCategoryName = categoryField.getText();
    }
    /**
     * Cathegory name detection.
     * @param event Left data in text label.
     */
    @FXML
    void categoryNameExited (MouseEvent event) {
        if (categoryField.getText().equals("")) {
            return;
        }
        shelfCategoryName = categoryField.getText();
    }
    /**
     * Type name detection.
     * @param event Entered data.
     */
    @FXML
    void nameTypeAction (ActionEvent event) {
        if (nameTypeField.getText().equals("")) {
            return;
        }
        shelfTypeName = nameTypeField.getText();
    }
    /**
     * Type name detection.
     * @param event Left data in text label.
     */
    @FXML
    void nameTypeExited (MouseEvent event) {
        if (nameTypeField.getText().equals("")) {
            return;
        }
        shelfTypeName = nameTypeField.getText();
    }
    /**
     * Shelf count detection.
     * @param event Entered data.
     */
    @FXML
    void shelfCountAction (ActionEvent event) {
        if (countField.getText().equals("")) {
            return;
        }
        shelfCount = Integer.parseInt(countField.getText());
    }
    /**
     * Shelf count detection.
     * @param event Left data in text label.
     */
    @FXML
    void shelfCountExited (MouseEvent event) {
        if (countField.getText().equals("")) {
            return;
        }
        shelfCount = Integer.parseInt(countField.getText());
    }
    /**
     * Weight type detection.
     * @param event Entered data.
     */
    @FXML
    void weightTypeAction (ActionEvent event) {
        if (weightTypeField.getText().equals("")) {
            return;
        }
        shelfWeight = Double.parseDouble(weightTypeField.getText());
    }
    /**
     * Weight type detection.
     * @param event Left data in text label.
     */
    @FXML
    void weightTypeExited (MouseEvent event) {
        if (weightTypeField.getText().equals("")) {
            return;
        }
        shelfWeight = Double.parseDouble(weightTypeField.getText());
    }
}
