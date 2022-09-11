package cz.fit.ijaproject.carriage;

import cz.fit.ijaproject.util.*;

import java.util.ArrayList;

/**
 * Module for work with Carriage accessible by Warehouse instance
 *
 * @author Tomas Hladky
 */
public class Carriage {

    public static final double MAX_CAPACITY = 250.0;

    /**
     * If this flag is on, search algorithm will return
     * null if its not possible fill all required
     * goods (carriage capacity overflow)
     */
    private boolean stopOnCapacity;

    /**
     * If this flag is on, search algorithm will return
     * null if there is less goods possible to get than
     * requested (not enough goods of blockType in warehouse)
     */
    private boolean stopOnNotEnoughGoods;

    /**
     * Determines, whether carriage is busy or not.
     * Cannot execute another task while is busy.
     */
    private boolean carriageBusy;

    private Point position;
    private final CarriagePath carriagePath;


    private final double maxCapacity;
    private double currentCapacity;

    private final ArrayList<Goods> goods;

    public Carriage(Point start) {
        this.position = start;
        carriagePath = new CarriagePath();

        stopOnCapacity = false;
        stopOnNotEnoughGoods = false;
        carriageBusy = false;

        maxCapacity = MAX_CAPACITY;
        currentCapacity = 0;

        goods = new ArrayList<>();
    }

    /**
     * Start carriage to resolve user/input selection,
     * find and load goods from warehouse and deliver to finish
     *
     * @param selection - User or file input selection of points
     * @return - success of execution
     */
    public boolean startWork(ArrayList<LoadGoods> selection) {
        if (!isCarriageBusy()) {
            setCarriageBusy(true);
            carriagePath.startWork(selection);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Request carriage to proceed next action and return its result
     *
     * @return - new action that carriage done. For more about actions see CarriagePath
     */
    public CarriageAction getNext() {
        CarriageAction carriageAction = carriagePath.getNext();

        switch (carriageAction.getAction()) {
            case MOVE:
                // Update position
                setPosition(carriageAction.getPosition());
                break;
            case LOAD:
                // Update Carriage capacity
                double newCapacity = getCurrentCapacity() + (carriageAction.getCount()
                        * carriageAction.getPosition().shelf.getType().getWeight());

                if (newCapacity > MAX_CAPACITY) {
                    carriageAction.setAction(AreaUtils.Action.CAPACITY_ERROR);
                    carriagePath.refresh();

                    setCarriageBusy(false);
                } else {
                    // Update carriage goods
                    goods.add(new Goods(carriageAction.getPosition().shelf.getType(),
                            carriageAction.getCount()));

                    // Update shelf goods count
                    int newShelfCount = carriageAction.getPosition().shelf.getCount() - carriageAction.getCount();
                    carriageAction.getPosition().shelf.setCount(newShelfCount);

                    setCurrentCapacity(newCapacity);
                }
                break;
            case MOVE_ERROR:
            case CARRIAGE_ERROR:
            case FINISH:
                // Free Carriage goods
                goods.clear();

                // Free Carriage capacity
                setCurrentCapacity(0);

                setCarriageBusy(false);
                break;
        }

        return carriageAction;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public ArrayList<Point> getCarriagePath() {
        return carriagePath.getCarriagePath();
    }

    public ArrayList<Goods> getGoods() {
        return goods;
    }

    public boolean isStopOnCapacity() {
        return stopOnCapacity;
    }

    public void setStopOnCapacity(boolean stopOnCapacity) {
        this.stopOnCapacity = stopOnCapacity;
    }

    public boolean isStopOnNotEnoughGoods() {
        return stopOnNotEnoughGoods;
    }

    public void setStopOnNotEnoughGoods(boolean stopOnNotEnoughGoods) {
        this.stopOnNotEnoughGoods = stopOnNotEnoughGoods;
    }

    public boolean isCarriageBusy() {
        return carriageBusy;
    }

    private void setCarriageBusy(boolean carriageBusy) {
        this.carriageBusy = carriageBusy;
    }

    public double getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }
}
