package cz.fit.ijaproject.warehouse;

/**
 * Type that defines goods type, its category and its weight
 *
 * @author Tomas Hladky
 * @author Jozef Makis
 */
public class Type {
    private final int id;
    private final double weight;
    private final String name;
    private final Category category;

    public Type(int id, double weight, String name, Category category) {
        this.id = id;
        this.weight = weight;
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }
}
