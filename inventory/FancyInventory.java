package farm.inventory;

import farm.core.FailedTransactionException;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.*;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

import java.util.*;

/**
 * A fancy inventory which stores products in stacks, enabling quantity information.
 * <p>
 * Introduces the concept of performing operations on multiple Products, such as removing 4 Eggs.
 * @stage3
 */
public class FancyInventory implements Inventory {
    private final Map<Barcode, List<Product>> stockedProducts = new HashMap<>();

    @Override
    public void addProduct(Barcode barcode, Quality quality) {
        try {
            addProduct(barcode, quality, 1);
        } catch (InvalidStockRequestException ignored) {
            // never thrown by a fancy inventory
        }
    }

    /**
     * Adds multiple of the product with corresponding barcode to the inventory.
     * @param barcode the barcode of the product to add.
     * @param quality the quality of added product.
     * @param quantity the amount of the product to add.
     */
    public void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        List<Product> stock = stockedProducts.getOrDefault(barcode, new ArrayList<>());
        stock.addAll(createProducts(barcode, quality, quantity));
        stockedProducts.putIfAbsent(barcode, stock);
    }

    @Override
    public boolean existsProduct(Barcode barcode) {
        return stockedProducts.containsKey(barcode) && !stockedProducts.get(barcode).isEmpty();
    }

    /**
     * Removes the highest quality product with corresponding barcode from the inventory.
     * <p>
     * Removes the product with the highest quality first. That is, if the inventory contains a REGULAR Egg and a GOLD Egg, the GOLD egg will be returned.
     * @param barcode The barcode of the product to be removed.
     * @return A list containing the removed product if it exists, else an empty list.
     */
    @Override
    public List<Product> removeProduct(Barcode barcode) {
        if (!existsProduct(barcode) || stockedProducts.get(barcode).isEmpty()) {
            return Collections.emptyList();
        }
        return List.of(getHighestQualityProduct(stockedProducts.get(barcode)));
    }

    /**
     * Removes a given number of products with corresponding barcode from the inventory, choosing the highest quality products possible.
     * <p>
     * Removes the products with the highest quality first. That is, if the inventory contains two REGULAR Eggs and a GOLD Egg, and two Eggs are removed the returned list will contain a GOLD Egg and a REGULAR Egg.
     * <p>
     * If there are not enough of a given product in the inventory it will return as many of said product as possible. That is if someone attempts to remove five Eggs but the Inventory only has four, all four Eggs will be removed. i.e {@code removeProduct(Barcode.EGG, quantity).size() <= quantity}.
     * @param barcode The barcode of the product to be removed.
     * @param quantity The total amount of the product to remove from the inventory.
     * @return A list containing the removed product if it exists, else an empty list.
     */
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {
        if (!existsProduct(barcode)) {
            return Collections.emptyList();
        }
        List<Product> toRemove = new ArrayList<>();
        if (getStockedQuantity(barcode) <= quantity) {
            toRemove = stockedProducts.get(barcode);
            stockedProducts.put(barcode, new ArrayList<>()); // remove all matching products
        } else { // remove matching products one by one
            for (int i = 0; i < quantity; i++) {
                toRemove.add(getHighestQualityProduct(stockedProducts.get(barcode)));
            }
        }
        return toRemove;
    }

    /**
     * Retrieves the full stock currently held in the inventory.
     * <p>
     * The returned list must be grouped by product type as per the order defined in {@link Barcode}.
     * <br>
     * That is, if there is 1 Wool, 1 Egg, and 2 Milk in the inventory, the returned list will be organised into 1 Egg, 2 Milk, 1 Wool. i.e {@code getAllProducts() == [Egg(), Milk(), Milk(), Wool()]}
     *
     * @hint See {@link Barcode#values()} about the order
     * @return An organised list containing all products currently stored in the inventory.
     */
    @Override
    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();
        for (Barcode type : Barcode.values()) {
            if (existsProduct(type)) {
                allProducts.addAll(stockedProducts.getOrDefault(type, Collections.emptyList()));
            }
        }
        return allProducts;
    }

    /**
     * Get the quantity of a specific product in the inventory.
     * @param barcode The barcode of the product.
     * @return The amount of the corresponding product currently in the inventory.
     */
    public int getStockedQuantity(Barcode barcode) {
        return stockedProducts.getOrDefault(barcode, Collections.emptyList()).size();
    }

    /** Private Helper Methods **/

    private List<Product> createProducts(Barcode barcode, Quality quality, int quantity) {
        List<Product> products = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            products.addLast(
                    switch (barcode) {
                        case Barcode.EGG -> new Egg(quality);
                        case Barcode.MILK -> new Milk(quality);
                        case Barcode.JAM -> new Jam(quality);
                        case Barcode.WOOL -> new Wool(quality);
                        case Barcode.BREAD -> new Bread(quality);
                        case Barcode.COFFEE -> new Coffee(quality);
                    }
            );
        }
        return products;
    }

    private Product getHighestQualityProduct(List<Product> products) {
        Product best = products.getFirst();
        for (Product candidate : products) {
            if (candidate.getQuality().equals(Quality.IRIDIUM)) {
                products.remove(candidate);
                return candidate; // early exit on first iridium candidate
            }
            if (candidate.getQuality().compareTo(best.getQuality()) > 0) {
                best = candidate;
            }
        }
        products.remove(best);
        return best;
    }
}
