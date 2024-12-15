package farm.files;

import farm.core.farmgrid.FarmGrid;
import farm.core.farmgrid.Grid;
import farm.core.farmgrid.GridConstructor;
import farm.core.farmgrid.PlantGrid;
import farm.core.farmgrid.items.AnimalItem;
import farm.core.farmgrid.items.PlantItem;
import farm.core.farmgrid.items.FarmItem;

import java.io.IOException;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
  File loader class, for loading all of the files!
 */
public class FileLoader {

    /**
     * Constructor for the FileLoader
     */
    public FileLoader() {
    }

    /**
     * Loads contents of the specified file into a Grid.
     * @param filename the String filename to read contents from.
     * @return a grid instance.
     * @throws IOException Honestly, no clue
     */
    public Grid load(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            //get first three lines to construct the grid
            String farmType = reader.readLine().trim();
            int rows = Integer.parseInt(reader.readLine().trim());
            int columns = Integer.parseInt(reader.readLine().trim());

            System.out.println(farmType + " " + rows + " " + columns);
            //make the actual grid
            GridConstructor gridConstructor = new GridConstructor();
            FarmGrid newGrid = gridConstructor.constructGrid(rows, columns, farmType);


            //for tracking row and column
            int row = 0;
            int col = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (row >= rows) {
                    row = 0;
                    col++;
                }
                if (!line.trim().isEmpty()) {
                    newGrid.loadValueIntoGrid(row, col, line);
                }
                row++;
            }
            return newGrid;
        }
    }
}
