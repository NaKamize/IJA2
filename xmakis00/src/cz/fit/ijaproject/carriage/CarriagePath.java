package cz.fit.ijaproject.carriage;

import cz.fit.ijaproject.carriage.astar.AStarAlg;
import cz.fit.ijaproject.carriage.astar.AStarNode;
import cz.fit.ijaproject.util.AreaUtils;
import cz.fit.ijaproject.util.CarriageAction;
import cz.fit.ijaproject.util.LoadGoods;
import cz.fit.ijaproject.util.Point;
import cz.fit.ijaproject.warehouse.Warehouse;

import java.util.ArrayList;

/**
 * Package private module that uses Carriage. Computes path, request next action
 * and return results back to Carriage
 * <p>
 * Carriage path simulation:
 * <p>
 * 1. Find nearest point to start point
 * <p>
 * 2. Find possible shortest path
 * <p>
 * 3. Set carriage current path he wants to go
 * <p>
 * 4. Iterate with getNext() to check if next move is possible
 * and return either following point with:
 * - action MOVE to move Carriage
 * - action MOVE_ERROR if no move to target is possible
 * - action LOAD to load goods to Carriage
 * - action NEW_PATH to count path on blockade or after goods load
 * - action FINISH to finish Carriage path and empty its content
 * - action CARRIAGE_ERROR to unknown error
 * (action CAPACITY_ERROR is set in Carriage class after content overflow detection)
 * <p>
 * 5. Repeat 4 until get action FINISH or ERROR
 *
 * @author Tomas Hladky
 */
class CarriagePath {

    private Warehouse warehouse;

    private ArrayList<LoadGoods> goodsToResolve;
    private LoadGoods currentGoods;
    private ArrayList<Point> carriagePath;  // Carriage current path

    public CarriagePath() {
        this.warehouse = Warehouse.getInstance();
        this.carriagePath = new ArrayList<>();
        currentGoods = null;
    }

    /**
     * Receive request from Carriage to resolve selection and start
     * loading goods
     *
     * @param selection - selection of (types and count) from user/input file
     */
    public void startWork(ArrayList<LoadGoods> selection) {
        this.goodsToResolve = selection;

        loadPath();
    }

    /**
     * Computes new path for Carriage where it will go.
     * Find nearest goods and compute nearest path to get there
     */
    private void loadPath() {
        currentGoods = findNearestLoadGoods(goodsToResolve);
        assert currentGoods != null;

        goodsToResolve.remove(currentGoods);

        carriagePath = AStarPath(currentGoods.getPosition());

        // debugPath(carriagePath);

        carriagePath = constructShortestPath(carriagePath);
    }

    /**
     * Proceed next carriage action.
     * Evaluate situations like new blockade has been created
     * on expected move position by Carriage. It needs to compute
     * a new path to its target.
     * More info about actions is in description of this class
     *
     * @return - next action that Carriage proceed
     */
    public CarriageAction getNext() {
        if (carriagePath == null) {
            return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.MOVE_ERROR);
        }

        if (carriagePath.isEmpty()) {
            if (goodsToResolve.isEmpty()) {
                if (warehouse.getCarriage().getPosition() == warehouse.getFinish()) {
                    // All goods gathered and carriage arrived to finished
                    refresh();
                    return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.FINISH);
                } else {
                    // All goods gathered, go to finish
                    this.goodsToResolve.add(0, new LoadGoods(warehouse.getFinish(), 0));
                    loadPath();
                    return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.NEW_PATH);
                }
            } else {
                // Count path to new goods
                loadPath();
                return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.NEW_PATH);
            }
        } else {
            Point followingPoint = carriagePath.get(0);
            carriagePath.remove(0);

            if (followingPoint.blockType == AreaUtils.BlockType.FREE) {
                return new CarriageAction(followingPoint, AreaUtils.Action.MOVE);
            } else if (followingPoint.blockType == AreaUtils.BlockType.SHELF) {
                return new CarriageAction(followingPoint, AreaUtils.Action.LOAD, currentGoods.getCount());
            } else if (followingPoint.blockType == AreaUtils.BlockType.BLOCK) {
                // Blockade on path, recount current path
                goodsToResolve.add(0, currentGoods);
                loadPath();
                return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.NEW_PATH);
            } else {
                return new CarriageAction(warehouse.getCarriage().getPosition(), AreaUtils.Action.CARRIAGE_ERROR);
            }
        }
    }

    /**
     * Count AStar path using Manhattan distance heuristic for Carriage in 4 directions
     *
     * @param target - target point to move Carriage
     * @return - path of points to target, null if there is no path possible
     */
    private ArrayList<Point> AStarPath(Point target) {
        ArrayList<AStarNode> open = new ArrayList<>();
        ArrayList<AStarNode> closed = new ArrayList<>();

        AStarNode start = new AStarNode(warehouse.getCarriage().getPosition());
        start.setG(0);
        start.setH(0);

        open.add(start);

        while (!open.isEmpty()) {
            AStarNode q = AStarAlg.findLowestF(open);
            assert q != null;

            open.remove(q);

            AStarNode up;
            AStarNode down;
            AStarNode right;
            AStarNode left;

            AStarNode[] successors = new AStarNode[AStarAlg.ASTAR_SIDES];

            if (q.getPosition().y + 1 >= 0 && q.getPosition().y + 1 < warehouse.getAreaHeight() &&
                    (warehouse.getArea()[q.getPosition().x][q.getPosition().y + 1].blockType == AreaUtils.BlockType.FREE ||
                            (warehouse.getArea()[q.getPosition().x][q.getPosition().y + 1].blockType == AreaUtils.BlockType.SHELF &&
                                    warehouse.getArea()[q.getPosition().x][q.getPosition().y + 1] == target)
                    )) {
                down = new AStarNode(warehouse.getArea()[q.getPosition().x][q.getPosition().y + 1]);
            } else {
                down = null;
            }

            if (q.getPosition().y - 1 >= 0 && q.getPosition().y - 1 < warehouse.getAreaHeight() &&
                    (warehouse.getArea()[q.getPosition().x][q.getPosition().y - 1].blockType == AreaUtils.BlockType.FREE ||
                            (warehouse.getArea()[q.getPosition().x][q.getPosition().y - 1].blockType == AreaUtils.BlockType.SHELF &&
                                    warehouse.getArea()[q.getPosition().x][q.getPosition().y - 1] == target)
                    )) {
                up = new AStarNode(warehouse.getArea()[q.getPosition().x][q.getPosition().y - 1]);
            } else {
                up = null;
            }

            if (q.getPosition().x + 1 >= 0 && q.getPosition().x + 1 < warehouse.getAreaWidth() &&
                    (warehouse.getArea()[q.getPosition().x + 1][q.getPosition().y].blockType == AreaUtils.BlockType.FREE ||
                            (warehouse.getArea()[q.getPosition().x + 1][q.getPosition().y].blockType == AreaUtils.BlockType.SHELF &&
                                    warehouse.getArea()[q.getPosition().x + 1][q.getPosition().y] == target)
                    )) {
                right = new AStarNode(warehouse.getArea()[q.getPosition().x + 1][q.getPosition().y]);
            } else {
                right = null;
            }

            if (q.getPosition().x - 1 >= 0 && q.getPosition().x - 1 < warehouse.getAreaWidth() &&
                    (warehouse.getArea()[q.getPosition().x - 1][q.getPosition().y].blockType == AreaUtils.BlockType.FREE ||
                            (warehouse.getArea()[q.getPosition().x - 1][q.getPosition().y].blockType == AreaUtils.BlockType.SHELF &&
                                    warehouse.getArea()[q.getPosition().x - 1][q.getPosition().y] == target)
                    )) {
                left = new AStarNode(warehouse.getArea()[q.getPosition().x - 1][q.getPosition().y]);
            } else {
                left = null;
            }

            successors[0] = up;
            successors[1] = down;
            successors[2] = right;
            successors[3] = left;

            for (int i = 0; i < AStarAlg.ASTAR_SIDES; i++) {
                if (successors[i] == null) {
                    continue;
                }

                successors[i].setG(q.getG() + 1);
                successors[i].setH(AStarAlg.countH(successors[i].getPosition(), target));

                if (successors[i].getPosition() == target) {
                    closed.add(q);
                    closed.add(successors[i]);

                    return AStarAlg.nodesToPoints(closed);
                }

                // Update
                AStarNode hasOpen = AStarAlg.findByPosition(open, successors[i].getPosition());
                AStarNode hasClosed = AStarAlg.findByPosition(closed, successors[i].getPosition());

                if (hasOpen == null) {
                    if (hasClosed == null) {
                        open.add(successors[i]);
                    } else {
                        continue;
                    }
                } else if (hasOpen.getF() < successors[i].getF()) {
                    hasOpen.setG(successors[i].getG());
                    hasOpen.setH(successors[i].getH());

                    // reorder
                    open.remove(hasOpen);
                    open.add(hasOpen);
                }
            }

            closed.add(q);
        }

        // Carriage blocked from all sides (no possible path)
        return null;
    }

    /**
     * Find nearest Point of goods from Carriage current position
     *
     * @param goods - List of goods that Carriage collects
     * @return - Nearest goods from list to Carriage
     */
    private LoadGoods findNearestLoadGoods(ArrayList<LoadGoods> goods) {
        LoadGoods nearestGoods = null;
        Point carriagePoint = warehouse.getCarriage().getPosition();

        if (goodsToResolve == null) {
            return null;
        }

        // Simple determination if point is closer
        for (LoadGoods loadGoods : goods) {
            if (nearestGoods == null) {
                nearestGoods = loadGoods;
            } else if (Math.abs(loadGoods.getPosition().x - carriagePoint.x) +
                    Math.abs(loadGoods.getPosition().y - carriagePoint.y) <
                    Math.abs(nearestGoods.getPosition().x - carriagePoint.x) +
                            Math.abs(nearestGoods.getPosition().y - carriagePoint.y)) {
                nearestGoods = loadGoods;
            }
        }

        return nearestGoods;
    }

    /**
     * Construct shortest path from closed list used in AStar.
     * Uses backtracking method from target to start.
     *
     * @param path - Path from close list
     * @return - shortest path to destination
     */
    public ArrayList<Point> constructShortestPath(ArrayList<Point> path) {
        if (path == null) {
            return null;
        }

        if (path.size() < 3) {
            return path;
        }

        // Create boolean list to mark which of them were visited and not visited
        ArrayList<Boolean> visitPoints = new ArrayList<>();

        for (int i = 0; i < path.size(); i++) {
            visitPoints.add(false);
        }

        ArrayList<Point> extractedPath = new ArrayList<>();

        Point start = path.get(0);
        Point destination = path.get(path.size() - 1);

        int dx = start.x - destination.x >= 0 ? 1 : -1;

        backtrackPath(dx, destination, start, path, visitPoints, extractedPath);

        // Add finish point on the end
        extractedPath.add(destination);

        return extractedPath;
    }

    private boolean backtrackPath(int dx, Point current, Point target, ArrayList<Point> list,
                                  ArrayList<Boolean> visited, ArrayList<Point> output) {

        int index = list.indexOf(current);

        if (index != -1) {
            visited.set(index, true);
        }

        if (current == target) {
            return true;
        } else if (index != -1) {
            if (current.x + dx >= 0 && current.x + dx < warehouse.getAreaWidth() &&
                    list.contains(warehouse.getArea()[current.x + dx][current.y]) &&
                    !visited.get(list.indexOf(warehouse.getArea()[current.x + dx][current.y]))) {
                // left / right (depends on dx)
                if (backtrackPath(dx, warehouse.getArea()[current.x + dx][current.y], target, list, visited, output)) {
                    output.add(warehouse.getArea()[current.x + dx][current.y]);
                    return true;
                }
            }

            if (current.y + 1 >= 0 && current.y + 1 < warehouse.getAreaHeight() &&
                    list.contains(warehouse.getArea()[current.x][current.y + 1]) &&
                    !visited.get(list.indexOf(warehouse.getArea()[current.x][current.y + 1]))) {
                // down
                if (backtrackPath(dx, warehouse.getArea()[current.x][current.y + 1], target, list, visited, output)) {
                    output.add(warehouse.getArea()[current.x][current.y + 1]);
                    return true;
                }
            }
            if (current.y - 1 >= 0 && current.y - 1 < warehouse.getAreaHeight() &&
                    list.contains(warehouse.getArea()[current.x][current.y - 1]) &&
                    !visited.get(list.indexOf(warehouse.getArea()[current.x][current.y - 1]))) {
                // up
                if (backtrackPath(dx, warehouse.getArea()[current.x][current.y - 1], target, list, visited, output)) {
                    output.add(warehouse.getArea()[current.x][current.y - 1]);
                    return true;
                }
            }
            if (current.x - dx >= 0 && current.x - dx < warehouse.getAreaWidth() &&
                    list.contains(warehouse.getArea()[current.x - dx][current.y]) &&
                    !visited.get(list.indexOf(warehouse.getArea()[current.x - dx][current.y]))) {
                // right / left (depends on dx)
                if (backtrackPath(dx, warehouse.getArea()[current.x - dx][current.y], target, list, visited, output)) {
                    output.add(warehouse.getArea()[current.x - dx][current.y]);
                    return true;
                }
            }

            // return false;
        }

        return false;
    }

    public ArrayList<Point> getCarriagePath() {
        return carriagePath;
    }

    /**
     * Return CarriagePath back to initial state
     */
    public void refresh() {
        goodsToResolve = null;
        currentGoods = null;
        carriagePath = null;
    }

    /**
     * Prints debug info about created path
     *
     * @param path - constructed path
     */
    private void debugPath(ArrayList<Point> path) {
        if (path == null) {
            System.out.println("Not found any possible path to target");
        } else {
            for (Point point : path) {
                System.out.printf("Pos: [%d, %d]\n", point.x, point.y);
            }
        }
    }
}
