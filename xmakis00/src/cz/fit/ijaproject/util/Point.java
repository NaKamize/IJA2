package cz.fit.ijaproject.util;

import cz.fit.ijaproject.warehouse.Shelf;

/**
 * Type that defines point on map, type and shelf if type is shelf (otherwise shelf is null)
 *
 * @author Tomas Hladky
 */
public class Point {

    public int x;
    public int y;
    public AreaUtils.BlockType blockType;
    public Shelf shelf; // May be null. Only refers to shelf when type is shelf}

    public Point(int x, int y, AreaUtils.BlockType blockType, Shelf shelf) {
        this.x = x;
        this.y = y;
        this.blockType = blockType;
        this.shelf = shelf;
    }
}
