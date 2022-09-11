package cz.fit.ijaproject.warehouse;

/**
 * Type that define category of type
 *
 * @author Tomas Hladky
 * @author Jozef Makis
 */
public class Category {
    private final int id;
    private final String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
