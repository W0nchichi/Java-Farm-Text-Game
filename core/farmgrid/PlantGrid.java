package farm.core.farmgrid;

import farm.core.UnableToInteractException;
import farm.core.farmgrid.items.PlantItem;
import farm.inventory.product.Product;
import farm.inventory.product.data.Quality;

/**
 * Refactor #8
 * Made PlantGrid as its own subclass of FarmGrid
 * So that they follow the S of SOLID principles better
 */
public class PlantGrid extends FarmGrid {

    /**
     * Default constructor for the FarmGrid, creating a plant farm.     * <p>
     * NOTE: whatever class you implement that extends Grid *must* have a constructor
     * with this signature for testing purposes.     *     * @param rows    the number of rows on the grid
     * @param columns the number of columns on the grid
     * @requires rows > 0 && columns > 0
     */
    public PlantGrid(int rows, int columns) {
        super(rows, columns, "plant");
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
            case "berry" -> getFarmState().get(row).set(column, new PlantItem("berry", symbol));
            case "coffee" -> getFarmState().get(row).set(column, new PlantItem("coffee", symbol));
            case "wheat" -> getFarmState().get(row).set(column, new PlantItem("wheat", symbol));
            default -> throw new IllegalArgumentException("You cannot place that on a plant farm!");
        }
        return true;
    }

    @Override
    public Product harvest(int row, int column) throws UnableToInteractException {
        //Get the product first, save that to a variable
        //And then resetplantstate
        //ANd then return the product
        if (!isValidPosition(row, column)) {
            throw new UnableToInteractException("You can't harvest this location");
        }

        // throw an exception if you try to harvest empty ground
        if (getFarmState().get(row).get(column).getName().equals("ground")) {
            throw new UnableToInteractException("You can't harvest an empty spot!");
        }

        PlantItem plantItem = (PlantItem) (getFarmState().get(row).get(column));

        if (plantItem == null) {
            return null;
        }

        Quality quality = getRandomQuality().getRandomQuality();
        //casting otherwise gives annoying

        Product harvest = plantItem.harvest(quality);
        plantItem.reset();
        return harvest;

    }

    /**
     *  Allows for interactable actions with AnimalItems
     * @param command the interaction to perform
     * @param row the row coordinate
     * @param column the column coordinate
     * @return a boolean if the command is successfully performed
     * @throws UnableToInteractException in the case where an unknown command is entered
     */
    public boolean interact(String command, int row, int column) throws UnableToInteractException {
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
