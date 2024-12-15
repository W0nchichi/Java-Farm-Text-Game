package farm.files;

import farm.core.farmgrid.AnimalGrid;
import farm.core.farmgrid.Grid;
import farm.core.farmgrid.PlantGrid;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * FileSaver - for saving all of your classes to be loaded!
 */
public class FileSaver {

    /**
     * Saves the contents of the provided grid and farmType to a file with the specified name.
     * @param filename the name of the file where the grid will be saved
     * @param grid the grid instance to save to a file
     * @throws IOException if an error occurs during file writing
     */
    public void save(String filename, Grid grid) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write the grid type (PlantGrid or AnimalGrid)
            if (grid instanceof PlantGrid) {
                writer.write("plant");
            } else if (grid instanceof AnimalGrid) {
                writer.write("animal");
            }
            writer.newLine();

            // Write rows and columns
            writer.write(String.valueOf(grid.getRows()));
            writer.newLine();
            writer.write(String.valueOf(grid.getColumns()));
            writer.newLine();

            // Write grid statistics
            List<List<String>> stats = grid.getStats();
            for (List<String> row : stats) {
                writer.write(String.join(",", row).trim());
                writer.newLine();
            }
        }
    }
}
