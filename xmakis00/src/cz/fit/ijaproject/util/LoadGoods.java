package cz.fit.ijaproject.util;

/**
 * Class describes Point of requested shelves
 * and count of goods from each to get
 *
 * @author Tomas Hladky
 */
public class LoadGoods {

    private final Point position;
    private final int count;

    public LoadGoods(Point position, int count) {
        this.position = position;
        this.count = count;
    }

    public Point getPosition() {
        return position;
    }

    public int getCount() {
        return count;
    }
}
