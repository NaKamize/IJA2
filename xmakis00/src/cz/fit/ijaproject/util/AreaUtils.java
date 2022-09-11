package cz.fit.ijaproject.util;

/**
 * Utility class to define enums that are used globally
 *
 * @author Tomas Hladky
 */
public class AreaUtils {
    /**
     * Defines type of block of Point
     */
    public enum BlockType {
        FREE,
        SHELF,
        BLOCK
    }

    /**
     * Defines actions that Carriage can perform
     */
    public enum Action {
        MOVE,   // move Carriage
        MOVE_ERROR, //no move to target is possible
        LOAD,   // load goods to Carriage
        NEW_PATH, // count path on blockade or after goods load
        FINISH, // finish Carriage path and empty its content
        CAPACITY_ERROR, // unknown error
        CARRIAGE_ERROR // set in Carriage class after content overflow detection
    }
}
