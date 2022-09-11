package cz.fit.ijaproject.carriage.astar;

import cz.fit.ijaproject.util.Point;

import java.util.ArrayList;

/**
 * Helper class for AStar algorithm
 *
 * @author Tomas Hladky
 */
public class AStarAlg {

    public static final int ASTAR_SIDES = 4;

    /**
     * Find node with lowest f
     *
     * @param list - list with nodes
     * @return - lowest f from list
     */
    public static AStarNode findLowestF(ArrayList<AStarNode> list) {
        if (list.isEmpty()) {
            return null;
        }

        AStarNode lowest = null;
        for (AStarNode node : list) {
            if (lowest == null) {
                lowest = node;
            } else if (node.getF() <= lowest.getF()) {
                lowest = node;
            }
        }
        return lowest;
    }

    /**
     * Find node with the same position (if exists)
     *
     * @param list     - list with ndoes
     * @param position - position to compare
     * @return - if exists then node with the same position otherwise null
     */
    public static AStarNode findByPosition(ArrayList<AStarNode> list, Point position) {
        AStarNode found = null;

        if (list.isEmpty()) {
            return null;
        }

        for (AStarNode node : list) {
            if (node.getPosition() == position) {
                found = node;
                break;
            }
        }

        return found;
    }

    /**
     * Extract positions from AStarNodes and return Points list
     *
     * @return - extracted positions list
     */
    public static ArrayList<Point> nodesToPoints(ArrayList<AStarNode> list) {
        ArrayList<Point> points = new ArrayList<>();

        for (AStarNode node : list) {
            points.add(node.getPosition());
        }

        return points;
    }

    /**
     * Manhattan distance heuristic
     *
     * @param current - Current node
     * @param target  - Finish node
     * @return - h
     */
    public static int countH(Point current, Point target) {
        return Math.abs(current.x - target.x) +
                Math.abs(current.y - target.y);
    }
}
