package farm.core.farmgrid;

import farm.core.UnableToInteractException;
import farm.core.farmgrid.items.AnimalItem;
import farm.inventory.product.Product;
import farm.inventory.product.data.Quality;

/**
 * Refactor #8
 * A FarmGrid that is specifically designed for animals and AnimalItems
 * So that they follow the S of SOLID principles better
 */
public class AnimalGrid extends FarmGrid {

    /**
     * Constructor for animal grid
     * @param rows no. of rows
     * @param cols no. of cols
     */
    public AnimalGrid(int rows, int cols) {
        super(rows, cols, "animal");
    }

    @Override
    public boolean place(int row, int column, char symbol) {
        // Check if the row and column are valid positions
        if (!isValidPosition(row, column)) {
            return false;
        }
        // get name of item based on character
        String itemName = getItemNameFromSymbol(symbol);
        if (itemName == null) {
            return false;
        }

        // Check if spot is already taken
        if (!isSpotEmpty(row, column)) {
            throw new IllegalStateException("Something is already there!");
        }
        switch (itemName) {
            case "chicken" -> getFarmState().get(row).set(column,
                    new AnimalItem("chicken", symbol));
            case "cow" -> getFarmState().get(row).set(column,
                    new AnimalItem("cow", symbol));
            case "sheep" -> getFarmState().get(row).set(column,
                    new AnimalItem("sheep", symbol));
            default -> throw new IllegalArgumentException("You cannot place that "
                    + "on an animal farm!");
        }
        return true;
    }

    @Override
    public Product harvest(int row, int column) throws UnableToInteractException {
        if (!isValidPosition(row, column)) {
            throw new UnableToInteractException("You can't harvest this location");
        }

        // throw an exception if you try to harvest empty ground
        if (getFarmState().get(row).get(column).getName().equals("ground")) {
            throw new UnableToInteractException("You can't harvest an empty spot!");
        }

        Quality quality = getRandomQuality().getRandomQuality();

        AnimalItem animalItem = (AnimalItem) (getFarmState().get(row).get(column));
        if (animalItem.getName() != null) {
            return animalItem.produce(quality);
        }
        return null;
    }

    /**
     * Feed an animal at the specified location.
     * @param row the row coordinate
     * @param col the column coordinate
     * @return true iff the animal was fed, else false.
     */
    public boolean feed(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }

        // if the position coordinate is an animal
        if (getFarmState().get(row).get(col) instanceof AnimalItem a) {
            a.feed();
            return true;
        }
        return false;
    }

    @Override
    public boolean interact(String command, int row, int column) throws UnableToInteractException {
        if (!isValidPosition(row, column)) {
            throw new UnableToInteractException("You can't interact this location");
        }
        // if feeding an animal
        if (command.equals("feed")) {
            return feed(row, column);
        }
        if (command.equals("end-day")) {
            this.endDay();
            return true;
        }
        if (command.equals("remove")) {
            this.remove(row, column);
            return true;
        }
        throw new UnableToInteractException("Unknown command: " + command);
    }


}
