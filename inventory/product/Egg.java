package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of an egg.
 * <br>
 * <b>Note: </b> See {@link Barcode#EGG} for product information such as price and name.
 * @stage1
 */
public class Egg extends Product {
    /**
     * Create an egg instance with no additional details.
     * <br>
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Egg() {
        super(Barcode.EGG);
    }

    /**
     * Create an egg instance with a quality value.
     * @param quality the quality level to assign to this egg.
     */
    public Egg(Quality quality) {
        super(Barcode.EGG, quality);
    }
}