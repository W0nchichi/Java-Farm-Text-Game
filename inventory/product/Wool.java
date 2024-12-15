package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of wool.
 * <br>
 * <b>Note: </b> See {@link Barcode#WOOL} for product information such as price and name.
 * @stage1
 */
public class Wool extends Product {

    /**
     * Create a wool instance with no additional details.
     * <br>
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Wool() {
        super(Barcode.WOOL);
    }

    /**
     * Create a wool instance with a specific quality value.
     * @param quality the quality level to assign to this wool.
     */
    public Wool(Quality quality) {
        super(Barcode.WOOL, quality);
    }
}