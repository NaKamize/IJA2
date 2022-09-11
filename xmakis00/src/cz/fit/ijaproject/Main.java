package cz.fit.ijaproject;

import java.nio.file.Paths;
import java.util.regex.Pattern;
import cz.fit.ijaproject.parser.MapParser;
import cz.fit.ijaproject.util.Point;
import cz.fit.ijaproject.warehouse.Warehouse;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    /**
     * This will open first scene where user needs to
     * put his files with map or data. We need to know
     * atleast mapsize, other thigs could be set by user.
     * @param primaryStage First scene with data input options.
     * @throws Exception Could not resolve scene file or wrong file with data.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Paths.get("./src/cz/fit/ijaproject/applayout/AppScene.fxml").normalize().toUri().toURL());
        primaryStage.setTitle("Warehouse");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        String mapData = null;
        String itemData = null;

        for (String arg : args) {
            if (Pattern.matches("^--map-data=.*", arg)) {
                mapData = arg.substring(11);
            } else if (Pattern.matches("^--item-data=.*", arg)) {
                itemData = arg.substring(12);
            } else {
                System.err.println("Wrong argument!");
                System.exit(1);
            }
        }

        mapData = Paths.get(".").normalize().toString() + mapData;
        itemData = Paths.get(".").normalize().toString() + itemData;


        MapParser mapparser = new MapParser(mapData, itemData);
        Warehouse warehouse = Warehouse.getInstance();
        warehouse.init(mapparser.retShelf(), mapparser.retTypes(), mapparser.retWareHouseWidth(),
                mapparser.retWareHouseHeight(),
                new Point(mapparser.getStartX(), mapparser.getStartY(), null, null),
                new Point(mapparser.getFinishX(), mapparser.getFinishY(), null, null));

        launch(args);
    }

}
