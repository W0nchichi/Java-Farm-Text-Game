package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of jam.
 * <br>
 * <b>Note: </b> See {@link Barcode#JAM} for product information such as price and name.
 * @stage1
 */
public class Jam extends Product {
    /**
     * Create a jam instance with no additional details.
     * <br>
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Jam() {
        super(Barcode.JAM);
    }

    /**
     * Create a jam instance with a quality value.
     * @param quality the quality level to assign to this jam.
     */
    public Jam(Quality quality) {
        super(Barcode.JAM, quality);
    }
}