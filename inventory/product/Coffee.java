package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

    /**
     * A class representing an instance of a coffee.
     * <br>
     * <b>Note: </b> See {@link Barcode#COFFEE} for product information such as price and name.
     */
    public class Coffee extends Product {
        /**
         * Create a coffee instance with no additional details.
         * <br>
         * Item quality is not specified, so will default to be REGULAR.
         */
        public Coffee() {
            super(Barcode.COFFEE);
        }

        /**
         * Create a coffee instance with a quality value.
         * @param quality the quality level to assign to this egg.
         */
        public Coffee(Quality quality) {
            super(Barcode.COFFEE, quality);
        }
    }
