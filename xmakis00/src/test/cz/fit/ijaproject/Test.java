package test.cz.fit.ijaproject;

import cz.fit.ijaproject.util.AreaUtils;
import cz.fit.ijaproject.util.CarriageAction;
import cz.fit.ijaproject.util.LoadGoods;
import cz.fit.ijaproject.util.Point;
import cz.fit.ijaproject.warehouse.Category;
import cz.fit.ijaproject.warehouse.Shelf;
import cz.fit.ijaproject.warehouse.Type;
import cz.fit.ijaproject.warehouse.Warehouse;

import java.util.ArrayList;

/**
 * Test class to show and demonstrate Carriage work - move on map, pathfinding, etc.
 * <p>
 * Demonstrate following:
 * - Create data that would be stored in data and loaded by xml parser
 * - Create warehouse instance
 * - Add blockades to map
 * - Request carriage to get some goods from shelves
 * - Compute effective path to each goods
 * - Avoid blockades and other shelves on path
 * - Load goods from each shelf
 * - Move to finish point
 *
 * @author Tomas Hladky
 */
public class Test {

    private Warehouse warehouse;
    private ArrayList<Shelf> shelves;
    private ArrayList<Type> types;
    private int width;
    private int height;
    private Point start;
    private Point finish;

    public Test() {
        loadTestData();

        this.warehouse = Warehouse.getInstance();
        warehouse.init(shelves, types, width, height, start, finish);

        addTestBlockades();

        testPath();
    }

    private void loadTestData() {
        shelves = new ArrayList<>();
        types = new ArrayList<>();
        Category category = new Category(0, "Zelenina");

        Type type1 = new Type(0, 0.5, "Zemiaky", category);
        Type type2 = new Type(1, 0.2, "Mrkva", category);
        Type type3 = new Type(2, 0.2, "Petržlen", category);

        Shelf shelf1 = new Shelf(0, 4, 3, type1, 12);
        Shelf shelf2 = new Shelf(1, 2, 8, type2, 2);
        Shelf shelf3 = new Shelf(2, 17, 17, type2, 56);
        Shelf shelf4 = new Shelf(3, 6, 12, type3, 10);

        shelves.add(shelf1);
        shelves.add(shelf2);
        shelves.add(shelf3);
        shelves.add(shelf4);

        types.add(type1);
        types.add(type2);
        types.add(type3);

        width = 18;
        height = 18;

        start = new Point(0, 0, AreaUtils.BlockType.FREE, null);
        finish = new Point(0, 17, AreaUtils.BlockType.FREE, null);
    }

    private void testPath() {
        ArrayList<Type> typesSelection = new ArrayList<>();
        typesSelection.add(warehouse.getTypes().get(0));
        typesSelection.add(warehouse.getTypes().get(1));

        ArrayList<Integer> count = new ArrayList<>();
        count.add(2);
        count.add(15);

        ArrayList<LoadGoods> loadGoods = warehouse.selectionToLoadGoods(typesSelection, count);
        warehouse.getCarriage().startWork(loadGoods);

        ArrayList<CarriageAction> carriageActions = new ArrayList<>();

        CarriageAction action = warehouse.getCarriage().getNext();
        while (action != null &&
                action.getAction() != AreaUtils.Action.FINISH &&
                action.getAction() != AreaUtils.Action.MOVE_ERROR &&
                action.getAction() != AreaUtils.Action.CAPACITY_ERROR &&
                action.getAction() != AreaUtils.Action.CARRIAGE_ERROR) {

            if (action.getAction() == AreaUtils.Action.LOAD) {
                carriageActions.add(action);
                drawOutput(carriageActions);
                carriageActions.clear();
            } else {
                carriageActions.add(action);
            }
            action = warehouse.getCarriage().getNext();
        }

        // Draw finish
        drawOutput(carriageActions);
        carriageActions.clear();
    }

    private void drawOutput(ArrayList<CarriageAction> carriageActions) {
        System.out.print("     ");
        for (int i = 0; i < warehouse.getAreaWidth(); i++) {
            System.out.printf("%02d | ", i);
        }
        System.out.print("\n");

        for (int y = 0; y < warehouse.getAreaHeight(); y++) {
            System.out.printf("%02d | ", y);

            for (int x = 0; x < warehouse.getAreaWidth(); x++) {
                boolean found = false;
                for (CarriageAction action : carriageActions) {
                    if (action.getPosition().x == x && action.getPosition().y == y) {
                        if (action.getAction() == AreaUtils.Action.MOVE) {
                            System.out.print(" X | ");
                        } else if (action.getAction() == AreaUtils.Action.LOAD) {
                            System.out.print(" L | ");
                        }

                        found = true;
                    }
                }

                if (!found) {
                    if (warehouse.getArea()[x][y].blockType == AreaUtils.BlockType.BLOCK) {
                        System.out.print(" B | ");
                    }
                    else if (warehouse.getArea()[x][y].blockType == AreaUtils.BlockType.SHELF) {
                        System.out.print(" S | ");
                    }
                    else {
                        System.out.print("   | ");
                    }
                }
            }

            System.out.print("\n");
        }

        System.out.printf("Carriage finish pos: [%d, %d]\n\n", warehouse.getCarriage().getPosition().x,
                warehouse.getCarriage().getPosition().y);
    }

    private void addTestBlockades() {
        warehouse.setBlockade(warehouse.getArea()[4][0]);
        warehouse.setBlockade(warehouse.getArea()[4][1]);
        warehouse.setBlockade(warehouse.getArea()[3][1]);
        warehouse.setBlockade(warehouse.getArea()[3][2]);
        warehouse.setBlockade(warehouse.getArea()[3][3]);
        warehouse.setBlockade(warehouse.getArea()[3][4]);
        warehouse.setBlockade(warehouse.getArea()[4][4]);
        warehouse.setBlockade(warehouse.getArea()[8][6]);
        warehouse.setBlockade(warehouse.getArea()[9][6]);
        warehouse.setBlockade(warehouse.getArea()[10][6]);
        warehouse.setBlockade(warehouse.getArea()[11][7]);
        warehouse.setBlockade(warehouse.getArea()[11][8]);
        warehouse.setBlockade(warehouse.getArea()[11][9]);
        warehouse.setBlockade(warehouse.getArea()[8][17]);
        warehouse.setBlockade(warehouse.getArea()[8][16]);
        warehouse.setBlockade(warehouse.getArea()[8][15]);
    }

    public static void main(String[] args) {
        new Test();
    }

}
