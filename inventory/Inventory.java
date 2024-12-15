package farm.inventory;

import farm.core.FailedTransactionException;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

import java.util.List;

/**
 * An interface representing the base requirements for an Inventory.
 * <p>
 * Handles adding and removing items from storage.
 * @stage2
 */
public interface Inventory {

    /**
     * Adds a new product with corresponding barcode to the inventory.
     * @param barcode The barcode of the product to add.
     * @param quality The quality of added product.
     */
    void addProduct(Barcode barcode, Quality quality);

    /**
     * Adds the specified quantity of the product with corresponding barcode to the inventory, 
     * provided that the implementing inventory supports adding multiple products at once.
     * If it does not, throws an {@link InvalidStockRequestException} reporting that the inventory is not fancy enough.
     * @param barcode the barcode of the product to add.
     * @param quality the quality of added product.
     * @param quantity the amount of the product to add.
     */
    void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException;
    
    /**
     * Determines if a product exists in the inventory with the given barcode.
     * @param barcode The barcode of the product to check.
     * @return true iff a product exists, else false.
     */
    boolean existsProduct(Barcode barcode);

    /**
     * Removes the first product with corresponding barcode from the inventory.
     * @param barcode The barcode of the product to be removed.
     * @return A list containing the removed product if it exists, else an empty list.
     */
    List<Product> removeProduct(Barcode barcode);

    /**
     * Removes the given number of products with corresponding barcode from the inventory, provided that
     * the implementing inventory supports removing multiple products at once.
     * If it does not, throws a {@link FailedTransactionException} reporting that the inventory is not fancy enough.
     * <p>
     * If there are not enough of a given product in the inventory it will return as many of said product as possible. That is if someone attempts to remove five Eggs but the Inventory only has four, all four Eggs will be removed. i.e {@code removeProduct(Barcode.EGG, quantity).size() <= quantity}.
     * @param barcode The barcode of the product to be removed.
     * @param quantity The total amount of the product to remove from the inventory.              
     * @return A list containing the removed product if it exists, else an empty list.
     */
    List<Product> removeProduct(Barcode barcode, int quantity) throws FailedTransactionException;
    
    /**
     * Retrieves the full stock currently held in the inventory.
     * @return A list containing all products currently stored in the inventory.
     */
    List<Product> getAllProducts();
}
