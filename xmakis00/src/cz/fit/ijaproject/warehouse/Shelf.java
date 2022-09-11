package cz.fit.ijaproject.warehouse;

import cz.fit.ijaproject.util.AreaUtils;
import cz.fit.ijaproject.util.Point;

/**
 * Type that defines Shelf, position, its goods type and count
 *
 * @author Tomas Hladky
 * @author Jozef Makis
 */
public class Shelf {
    private final int id;
    private final Point position;

    private Type type;
    private int count;

    public Shelf(int id, int x, int y, Type type, int count) {
        this.id = id;
        this.position = new Point(x, y, AreaUtils.BlockType.SHELF, this);
        this.type = type;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public Point getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public void setType (Type type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
