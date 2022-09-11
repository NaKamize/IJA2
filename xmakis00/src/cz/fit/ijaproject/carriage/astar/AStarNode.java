package cz.fit.ijaproject.carriage.astar;

import cz.fit.ijaproject.util.Point;

/**
 * Type class that defines AStar Node
 *
 * @author Tomas Hladky
 */
public class AStarNode {

    private final Point position;
    private int g;
    private int h;

    public AStarNode(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getG() {
        return g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getH() {
        return h;
    }

    public int getF() {
        return g + h;
    }
}
