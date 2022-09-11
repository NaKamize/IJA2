package cz.fit.ijaproject.util;

import cz.fit.ijaproject.warehouse.Type;

/**
 * Similar to Shelf except this it is for Carriage and has no Point on map
 * Defines type of goods and count of it
 *
 * @author Tomas Hladky
 */
public class Goods {

    private final Type type;
    private int count;

    public Goods(Type type, int count) {
        this.type = type;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
