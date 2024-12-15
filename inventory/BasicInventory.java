package farm.inventory;

import farm.core.FailedTransactionException;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.*;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A very basic inventory that both stores and handles products individually.
 * <p>
 * Only supports operation on single Products at a time.
 * <p>
 * @stage2
 */
public class BasicInventory implements Inventory {
    private final ArrayList<Product> stockedProducts = new ArrayList<>();

    @Override
    public void addProduct(Barcode barcode, Quality quality) {
        stockedProducts.add(createProduct(barcode, quality));
    }


    /**
     * Throws an {@link InvalidStockRequestException} with the message:
     * <p>
     * <pre style="color:#00CC00">"Current inventory is not fancy enough. Please supply products one at a time."</pre>
     * <p>
     * @param barcode the product type to add to the inventory.
     * @param quality the quality of the product to add to the inventory.
     * @param quantity the number of products to add to the inventory.
     * @throws InvalidStockRequestException always, since Basic inventories never support quantities > 1.
     */
    @Override
    public void addProduct(Barcode barcode, Quality quality, int quantity)
                throws InvalidStockRequestException {
        throw new InvalidStockRequestException("Current inventory is not fancy enough. "
                + "Please supply products one at a time.");
    }

    @Override
    public boolean existsProduct(Barcode barcode) {
        for (Product product : stockedProducts) {
            if (product.getBarcode() == barcode) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Product> removeProduct(Barcode barcode) {
        for (Product product : stockedProducts) {
            if (product.getBarcode() == barcode) {
                stockedProducts.remove(product);
                return List.of(product);
            }
        }
        return Collections.emptyList();
    }
    
    /**
     * Throws an {@link FailedTransactionException} with the message:
     * <p>
     * <pre style="color:#00CC00">"Current inventory is not fancy enough. Please purchase products one at a time."</pre>
     * <p>
     * @param barcode the product type to add to the inventory.
     * @throws FailedTransactionException always, since Basic inventories never support quantities > 1.
     */
    @Override
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {
        throw new FailedTransactionException("Current inventory is not fancy enough. "
                + "Please purchase products one at a time.");
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(stockedProducts);
    }

    private Product createProduct(Barcode barcode, Quality quality) {
        return switch (barcode) {
            case Barcode.EGG -> new Egg(quality);
            case Barcode.MILK -> new Milk(quality);
            case Barcode.JAM -> new Jam(quality);
            case Barcode.WOOL -> new Wool(quality);
            case Barcode.BREAD -> new Bread(quality);
            case Barcode.COFFEE -> new Coffee(quality);
        };
    }
}
