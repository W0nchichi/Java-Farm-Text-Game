package farm.sales;

import farm.inventory.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A shopping cart that stores the customer products until they check out.
 * @stage1
 */
public class Cart {
    private final List<Product> toPurchase = new ArrayList<>();

    /**
     * Adds a given product to the shopping cart.
     * @param product the product to add.
     */
    public void addProduct(Product product) {
        toPurchase.add(product);
    }

    /**
     * Retrieves all the products in the Cart in the order they were added.
     * @return a list of all products in the cart
     * @ensures the returned list is a shallow copy and cannot modify the original cart
     */
    public List<Product> getContents() {
        return new ArrayList<>(toPurchase);
    }

    /**
     * Empty out the shopping cart.
     */
    public void setEmpty() {
        toPurchase.clear();
    }

    /**
     * Returns if the cart is empty
     * @return true iff there is nothing in the cart, else false.
     */
    public boolean isEmpty() {
        return toPurchase.isEmpty();
    }
}
