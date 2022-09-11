package cz.fit.ijaproject.warehouse;

import cz.fit.ijaproject.carriage.Carriage;
import cz.fit.ijaproject.util.AreaUtils;
import cz.fit.ijaproject.util.LoadGoods;
import cz.fit.ijaproject.util.Point;

import java.util.ArrayList;

/**
 * Singleton instance which store all info about warehouse, its data, carriage
 * and is accessed by all other modules
 *
 * @author Tomas Hladky
 * @author Jozef Makis
 */
public class Warehouse {

    private static Warehouse instance = null;

    private int areaWidth;
    private int areaHeight;

    private ArrayList<Shelf> shelves;
    private ArrayList<Type> types;

    private Point[][] area;
    private Point start;
    private Point finish;

    private Carriage carriage;

    public Warehouse() {
        this.shelves = new ArrayList<>();
        this.area = null;
        this.start = null;
        this.finish = null;
    }

    public static Warehouse getInstance() {
        if (instance == null) {
            instance = new Warehouse();
        }

        return instance;
    }

    public void init(ArrayList<Shelf> shelves, ArrayList<Type> types,
                     int width, int height, Point start, Point finish) {
        this.shelves = shelves;
        this.types = types;
        this.start = start;
        this.finish = finish;
        this.areaWidth = width;
        this.areaHeight = height;

        this.carriage = new Carriage(start);

        this.area = generateArea();
    }

    /**
     * Generate 2D array where points occupied by shelves are their
     * reference and also for start and finish points
     *
     * @return 2D points array of warehouse area
     */
    private Point[][] generateArea() {
        Point[][] area = new Point[areaWidth][areaHeight];

        for (int x = 0; x < areaWidth; x++) {
            for (int y = 0; y < areaHeight; y++) {
                boolean found = false;
                for (Shelf shelf : shelves) {
                    if (shelf.getX() == x && shelf.getY() == y) {
                        area[x][y] = shelf.getPosition();
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    if (x == start.x && y == start.y) {
                        area[x][y] = start;
                    } else if (x == finish.x && y == finish.y) {
                        area[x][y] = finish;
                    } else {
                        area[x][y] = new Point(x, y, AreaUtils.BlockType.FREE, null);
                    }
                }
            }
        }

        return area;
    }

    /**
     * Create LoadGoods array describing positions and count
     * of goods to get from each
     *
     * @param types - selected types from user or input file
     * @param count - count of each requested blockType
     * @return - constructed LoadGoods list
     * or null if not enough goods in shelves (depends on Carriage - stopOnNotEnoughGoods)
     */
    public ArrayList<LoadGoods> selectionToLoadGoods(ArrayList<Type> types, ArrayList<Integer> count) {
        assert types != null;
        assert count != null;

        ArrayList<LoadGoods> loadGoodsSelection = new ArrayList<>();
        double carriageCapacityCounter = 0;

        for (int i = 0; i < types.size(); i++) {
            int remaining = count.get(i);
            boolean isEnough = false;
            for (Shelf shelf : shelves) {
                if (shelf.getType() == types.get(i)) {
                    if (shelf.getCount() > remaining) {
                        LoadGoods loadGoods = new LoadGoods(shelf.getPosition(), remaining);
                        loadGoodsSelection.add(loadGoods);
                        carriageCapacityCounter += remaining * shelf.getType().getWeight();
                        isEnough = true;
                        break;
                    } else {
                        // Shelf doesn't have enough count of requested blockType, get all what it has
                        remaining -= shelf.getCount();
                        LoadGoods loadGoods = new LoadGoods(shelf.getPosition(), shelf.getCount());
                        loadGoodsSelection.add(loadGoods);
                        carriageCapacityCounter += shelf.getCount() * shelf.getType().getWeight();
                    }
                }
            }

            if (!isEnough && getCarriage().isStopOnNotEnoughGoods()) {
                return null;
            }

            if (carriageCapacityCounter > carriage.getMaxCapacity() && getCarriage().isStopOnCapacity()) {
                return null;
            }
        }

        return loadGoodsSelection;
    }

    public int getAreaWidth() {
        return areaWidth;
    }

    public int getAreaHeight() {
        return areaHeight;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getFinish() {
        return finish;
    }

    public Point[][] getArea() {
        return area;
    }

    public ArrayList<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(ArrayList<Shelf> shelves) {
        this.shelves = shelves;
    }

    public ArrayList<Type> getTypes() {
        return types;
    }

    /**
     * Set blockade on specified position
     *
     * @param position - position where place blockade
     * @return -  boolean about success of operation
     */
    public boolean setBlockade(Point position) {
        if (position.x >= 0 && position.x < getAreaWidth() &&
                position.y >= 0 && position.y < getAreaHeight() &&
                position.blockType == AreaUtils.BlockType.FREE) {
            area[position.x][position.y].blockType = AreaUtils.BlockType.BLOCK;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove blockade from specified position
     *
     * @param position - position where remove blockade
     * @return - boolean about success of operation
     */
    public boolean removeBlockade(Point position) {
        if (position.x >= 0 && position.x < getAreaWidth() &&
                position.y >= 0 && position.y < getAreaHeight() &&
                position.blockType == AreaUtils.BlockType.BLOCK) {
            area[position.x][position.y].blockType = AreaUtils.BlockType.FREE;
            return true;
        } else {
            return false;
        }
    }

    public void setTypes(ArrayList<Type> types) {
        this.types = types;
    }

    public Carriage getCarriage() {
        return carriage;
    }
}
