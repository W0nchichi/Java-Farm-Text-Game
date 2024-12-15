package farm.inventory.product.data;

/**
 * Collection of constant barcodes representing all product types a farm can choose to stock.
 * <p>
 * A barcode identifies a specific type of product, and is common to all individual products
 * of that type (e.g. all eggs have the same barcode). Barcodes also store other constant data
 * associated with that type of product, such as its display name and base price.
 * <p>
 * <b>Note:</b> Barcodes are (obviously) typically numeric, but for our purposes enums serve
 * the same purpose without requiring that we write a unique integer generator or similar. We 
 * could of course make a method to give us an equivalent numeric code that more closely resembles
 * a real physical barcode, but that would be redundant, since we can use the enum members 
 * themselves anywhere that such a code might be expected.
 * @provided
 */
public enum Barcode {
    EGG("egg", 50),
    MILK("milk", 440),
    JAM("jam", 670),
    WOOL("wool", 2850),
    COFFEE("coffee", 750),
    BREAD("bread", 980),
    ;


    /**
     * Retrieve the display name for products matching this barcode.
     * @return The display name associated with this barcode.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retrieve the base price of products matching this barcode.
     * <br>
     * <b>Note:</b> The base price is in cents.
     * @return The base price associated with this barcode.
     */
    public int getBasePrice() {
        return basePrice;
    }

    /**
     * The display name used for products of this type when visual/textual representation
     * is needed, such as in labels for items in a farm shop's shelves.
     */
    private final String displayName;
    private final int basePrice;

    Barcode(String displayName, int basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }
}
