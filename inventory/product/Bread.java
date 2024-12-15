package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of bread.
 * <br>
 * <b> Note: </b> See {@link Barcode#BREAD} for product information such as price and name.
 */
public class Bread extends Product{
    /**
     * Create a bread instance with no additional details.
     * <br>
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Bread() {
        super(Barcode.BREAD);
    }

    /**
     * Create a bread instance with a quality value.
     * @param quality the quality level to assign to this egg.
     */
    public Bread(Quality quality) {
        super(Barcode.BREAD, quality);
    }
}
