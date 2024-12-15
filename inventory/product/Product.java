package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import java.util.Objects;

/**
 * An abstract class representing an instance of a product.
 * <p>
 * Each product is a single instance of a specific item.
 * @stage1
 */
public abstract class Product {
    private final Barcode type;
    private final Quality quality;
    private final int basePrice;

    /**
     * Create an instance of a product with the specified barcode.
     * Item quality is not specified, so will default to be REGULAR.
     * @param barcode Identifier for this product.
     * @hidden
     */
    protected Product(Barcode barcode) {
        this(barcode, Quality.REGULAR);
    }

    /**
     * Create an instance of a product with the specified barcode and quality.
     * @param barcode Identifier for this product.
     * @param quality Quality level to assign to this product.
     * @hidden
     */
    protected Product(Barcode barcode, Quality quality) {
        this.type = barcode;
        this.quality = quality; 
        this.basePrice = type.getBasePrice();
    }

    /**
     * Accessor method for the product's identifier.
     * @return the identifying Barcode for this product.
     */
    public Barcode getBarcode() {
        return this.type;
    }

    /**
     * Retrieve the product's display name, for visual/textual representation.
     * @return the product's display name.
     */
    public String getDisplayName() {
        return type.getDisplayName();
    }

    /**
     * Retrieve the product's quality.
     * @return the quality level for this product.
     */
    public Quality getQuality() {
        return this.quality;
    }

    /**
     * Retrieve the products base sale price.
     * @return the price of the product. In cents.
     */
    public int getBasePrice() {
        return basePrice;
    }

    /**
     * Returns a string representation of this product class. The representation contains the display name of the product, followed by its base price and quality. It is in the form {@code <name>:<price>c *<quality>*}
     * <p>
     * <b>Example output: </b>
     * <br>
     * <em>Note: </em> This example product is not a real product and is instead just used to demonstrate the format.
     * <p>
     * <pre style="color:#00CC00">"bread: 240c *IRIDIUM*"</pre>
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        return String.format("%s: %sc *%s*", getDisplayName(), getBasePrice(), getQuality());
    }

    /**
     * If two instances of product are equal to each other. Equality is defined by having the same barcode, and quality.
     * @param obj The object with which to compare
     * @return true iff the other object is a product with the same barcode, and quality as the current product.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Product product = (Product) obj;
        return getBarcode() == product.getBarcode() && getQuality() == product.getQuality();
    }

    /**
     * A hashcode method that respects the {@link Product#equals(Object)} method.
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, basePrice, quality);
    }

}
