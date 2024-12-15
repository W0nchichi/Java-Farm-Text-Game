package farm.core.farmgrid;

/**
 * The {@code GridConstructor} class is responsible for constructing different types of farm grids
 * based on the provided farm type. It can create either a {@code PlantGrid} or an {@code AnimalGrid},
 * depending on the input parameters.
 */
public class GridConstructor {

    /**
     * Default constructor for {@code GridConstructor}.
     * Initializes an instance of the class that can be used to create different types of farm grids.
     */
    public GridConstructor() {

    }

    /**
     * Constructs a {@code FarmGrid} based on the provided type, number of rows, and number of columns.
     * This method returns either a {@code PlantGrid} or an {@code AnimalGrid} depending on the specified {@code farmType}.
     *
     * @param rows     the number of rows for the grid
     * @param cols     the number of columns for the grid
     * @param farmType the type of farm grid to construct ("plant" for {@code PlantGrid}, "animal" for {@code AnimalGrid})
     * @return a {@code FarmGrid} instance of the specified type, or {@code null} if the farm type is not recognized
     */
    public FarmGrid constructGrid(int rows, int cols, String farmType) {
        switch (farmType) {
            case "plant" -> {
                return new PlantGrid(rows, cols);
            }
            case "animal" -> {
                return new AnimalGrid(rows, cols);
            }
            default -> {
                return null;
            }
        }
    }
}

