package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of milk.
 * <br>
 * <b>Note: </b> See {@link Barcode#MILK} for product information such as price and name.
 * @stage1
 */
public class Milk extends Product {
    /**
     * Create a milk instance with no additional details.
     * <br>
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Milk() {
        super(Barcode.MILK);
    }

    /**
     * Create a milk instance with a quality value.
     * @param quality the quality level to assign to this milk.
     */
    public Milk(Quality quality) {
        super(Barcode.MILK, quality);
    }
}