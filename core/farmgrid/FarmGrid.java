package farm.core.farmgrid;

import farm.core.UnableToInteractException;
import farm.inventory.product.*;
import farm.inventory.product.data.RandomQuality;
import farm.core.farmgrid.items.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This is my implimentation of the FarmGrid, which has been refactored._.
 * It's used to create a farmGrid of FarmItem objects which can do stuff!
 *  :DDDDD
 */
public abstract class FarmGrid implements Grid {

    private List<List<FarmItem>> farmState;
    private final int rows;
    private final int columns;
    private final String farmType;

    // randomQuality is used to help you generate quality for products
    private final RandomQuality randomQuality;


    /**
     * Constructor for the FarmGrid, creating a farm of specified type.
     * @param rows    the number of rows on the grid
     * @param columns the number of columns on the grid
     * @requires rows > 0 && columns > 0
     */
    public FarmGrid(int rows, int columns, String farmType) {

        this.farmType = farmType;

        this.rows = rows;
        this.columns = columns;

        farmState = new ArrayList<List<FarmItem>>();

        this.randomQuality = new RandomQuality();

        // populate the initial farm with empty ground
        for (int i = 0; i < rows; i++) {
            List<FarmItem> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add(new FarmItem());
            }
            farmState.add(row);
        }
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public int getColumns() {
        return this.columns;
    }

    /**
     * :DDDD
     * @return the farmState as a List of a List of FarmItem's
     */
    public List<List<FarmItem>> getFarmState() {
        return this.farmState;
    }

    /**
     * get da farm Type :D
     * @return the farmType as a String
     */
    public String getFarmType() {
        return this.farmType;
    }

    /**
     * get a Random Quality :D
     * @return a randomQuality as a RandomQuality
     */
    public RandomQuality getRandomQuality() {
        return this.randomQuality;
    }

    @Override
    public List<List<String>> getStats() {
        List<List<String>> stats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                stats.add(farmState.get(i).get(j).getStyled());
            }
        }
        return stats;
    }

    //-------------------------------------
    //+++++++++++Helper Methods++++++++++++
    // -------------------------------------
    /**
     * helper method
     * check if the place is a valid position (not null)
     * @return true if the position is within the bounds
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < this.rows && col >= 0 && col < this.columns;
    }

    protected boolean isSpotEmpty(int row, int column) {
        return farmState.get(row).get(column).getName().equals("ground");
    }

    protected String getItemNameFromSymbol(char symbol) {
        return switch (symbol) {
            case '.' -> "berry";  // Return Berry (PlantItem)
            case ':' -> "coffee";  // Return CoffeeBean (PlantItem)
            case '\u1F34' -> "wheat";   // Return Wheat (PlantItem)
            case '\u09EC' -> "chicken";  // Return Chicken (AnimalItem)
            case '\u096A' -> "cow";      // Return Cow (AnimalItem)
            case '\u0D94' -> "sheep";    // Return Sheep (AnimalItem)
            default -> null;  // If no matching symbol, return null
        };
    }

    //-------------------------------------
    //++++++++++FarmGrid Methods+++++++++++
    // -------------------------------------
    @Override
    public boolean place(int row, int column, char symbol) {
        // Check if the row and column are valid positions
        if (isValidPosition(row, column)) {
            if (isSpotEmpty(row, column)) {
                farmState.get(row).set(column, new FarmItem());
                return true;
            }
        }
        return false;
    }

    /**
     * remove whatever is at the given row/column
     */
    public void remove(int row, int column) {
        if (!isValidPosition(row, column)) {
            return;
        }
        // replace the spot with empty ground
        farmState.get(row).set(column, new FarmItem());
    }

    @Override
    public String farmDisplay() {
        StringBuilder display = new StringBuilder();
        String horizontalFence = "-".repeat((this.columns * 2) + 3) + System.lineSeparator();
        display.append(horizontalFence);

        for (List<FarmItem> row : farmState) {
            display.append("| ");
            for (FarmItem farmItem : row) {
                display.append(farmItem.getSymbol()).append(" ");
            }
            display.append("|").append(System.lineSeparator());
        }

        display.append(horizontalFence);
        return display.toString();
    }

    @Override
    public Product harvest(int row, int column) throws UnableToInteractException {
        return null;
    }

    /**
     * Ends the current day, and moves farm to the next day.
     * For plants, this grows the plant if possible.
     * For animals, this sets fed and collection status to false.
     */
    public void endDay() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                FarmItem farmItem = farmState.get(i).get(j);
                farmItem.endDay();
            }
        }
    }

    /**
     * loads values for FileLoader
     * @param row the row of placement
     * @param column the column of placement
     */
    public void  loadValueIntoGrid(int row, int column, String line) {
        String[] splitLine = line.split(",");
        String itemName = splitLine[0];

        switch (itemName) {
            case "ground":
                farmState.get(row).set(column, new FarmItem());  // Empty ground
                break;
            case "cow", "chicken", "sheep":
                char animalSymbol = splitLine[1].trim().charAt(0);
                AnimalItem animal = new AnimalItem(itemName, animalSymbol);
                farmState.get(row).set(column, animal);
                break;
            case "berry", "wheat", "coffee":
                char plantSymbol = splitLine[1].trim().charAt(0);
                farmState.get(row).set(column, new PlantItem(itemName, plantSymbol));
                break;
        }
    }
}