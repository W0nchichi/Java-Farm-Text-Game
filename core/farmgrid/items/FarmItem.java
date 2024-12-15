package farm.core.farmgrid.items;

import farm.inventory.product.data.Quality;

import java.util.List;

/**
 * The {@code FarmItem} class represents a generic item on the farm grid.
 * This could be an empty ground tile or any other item, such as plants or animals, that extend this class.
 * It provides basic functionality for retrieving item information like name, symbol, and styling.
 */
public class FarmItem {
    private final String name;
    private char symbol;
    private Quality quality;

    /**
     * Constructor for a {@code FarmItem} with a specified name and symbol.
     *
     * @param name   the name of the farm item (e.g., "wheat", "cow", "ground")
     * @param symbol the symbol representing the farm item on the grid
     */
    public FarmItem(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Default constructor for a {@code FarmItem} representing an empty ground tile.
     * Initializes the item with "ground" as its name and a space character as its symbol.
     */
    public FarmItem() {
        this.name = "ground";
        this.symbol = ' ';
    }

    /**
     * Retrieves the name of the farm item.
     *
     * @return the name of the farm item
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the symbol representing the farm item.
     *
     * @return the symbol of the farm item
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns a list of styled information about the farm item.
     * This is typically used for display purposes. For an empty ground tile, it returns "ground" and a space character.
     *
     * @return a list containing styled information about the farm item
     */
    public List<String> getStyled() {
        return List.of("ground", " ");
    }

    /**
     * Needed to set Symbol after making symbol private
     * @param symbol the symbol to be set
     */
    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns a string representation of the farm item, including its name and symbol.
     *
     * @return a string representation of the farm item
     */
    @Override
    public String toString() {
        return getName() + ", " + getSymbol();
    }

    /**
     * Simulates the end of the day for the farm item. In the base {@code FarmItem} class, this method does nothing.
     * It is overridden by subclasses that need to perform specific end-of-day actions, such as growing or resetting status.
     */
    public void endDay() {
        return;
    }
}
