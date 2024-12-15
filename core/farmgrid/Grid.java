package farm.core.farmgrid;

import farm.core.UnableToInteractException;
import farm.inventory.product.Product;
import java.util.List;

/**
 * Interface describing the required methods for a grid-based display.
 */
public interface Grid {

    /**
     * Interact with the specified coordinate, based on the command provided.
     * Used for interactions other than placing, removing, and harvesting.
     * @param command the interaction to perform
     * @param row the row coordinate
     * @param column the column coordinate
     * @return true iff the interaction was successful.
     */
    boolean interact(String command, int row, int column) throws UnableToInteractException;

    /**
     * Place an item on the grid at the given coordinate, based on the symbol identifying it.
     * An item cannot be placed if another item already exists at that coordinate.
     * @param row the row coordinate
     * @param column the column coordinate
     * @param symbol character representing the item to be placed
     * @return true iff the item was successfully placed.
     */
    boolean place(int row, int column, char symbol);

    /**
     * Attempts to harvest the product at the specified coordinate, placing the
     * resulting product into the inventory.
     * @param row row coordinate
     * @param column column coordinate
     * @return product gathered.
     */
    Product harvest(int row, int column) throws UnableToInteractException;

    /**
     * Generates the grid display of the farm as a String.
     */
    String farmDisplay();

    /**
     * Generates information about each position on the grid, and the string representation
     * of the grid itself.
     * @return List containing the type and symbol of item in each position.
     * For plants, this should include stage, for animals, fed and collection status.
     * <p>
     * Example:
     * [[berry, @, stage 3], [berry, ., stage 0], [berry, ., stage 1],
     * [coffee, %, stage 4], [@, ., ., %]]
     */
    List<List<String>> getStats();

    /**
     * @return the number of rows in the grid.
     */
    int getRows();

    /**
     * @return the number of columns in the grid.
     */
    int getColumns();
}
