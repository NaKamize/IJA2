package cz.fit.ijaproject.util;

/**
 * Class describes Carriage movement on map and his actions
 *
 * @author Tomas Hladky
 */
public class CarriageAction {

    private final Point position;
    private AreaUtils.Action action;
    private final int count;

    public CarriageAction(Point position, AreaUtils.Action action) {
        this.position = position;
        this.action = action;
        count = -1;
    }

    public CarriageAction(Point position, AreaUtils.Action action, int count) {
        this.position = position;
        this.action = action;
        this.count = count;
    }

    public Point getPosition() {
        return position;
    }

    public AreaUtils.Action getAction() {
        return action;
    }

    /**
     * This update method should be called only from
     * Carriage to update from LOAD -> ERROR
     * in case of overflow max Carriage capacity
     *
     * @param action - current action
     */
    public void setAction(AreaUtils.Action action) {
        this.action = action;
    }

    /**
     * Used in Action LOAD to get count of goods to get from shelf
     *
     * @return - number of goods to get from shelf
     */
    public int getCount() {
        return count;
    }
}
