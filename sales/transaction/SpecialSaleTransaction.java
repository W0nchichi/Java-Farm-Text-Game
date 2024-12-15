package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.sales.ReceiptPrinter;

import java.util.*;


/**
 * A transaction type that builds on the functionality of a categorised transaction, allowing
 * store-wide discounts to be applied to all products of a nominated type.
 * <p>
 * This can be thought of
 * as akin to putting on a special sale such as a seasonal special for lower price jam, or an 
 * end-of-year sale discounting all products. 
 * <p>
 * The resulting receipt takes the same base format as that off a categorised transaction, with 
 * an additional line appended to entries for any product types that have a non-zero discount 
 * indicating the percentage they will be discounted by, and an additional section below the 
 * overall transaction total where non-zero overall savings are reported.
 * @stage1
 */
public class SpecialSaleTransaction extends CategorisedTransaction {
    private final Map<Barcode, Integer> discounts;

    /**
     * Construct a new special sale transaction for an associated customer, with an 
     * empty set of discounts (i.e. no products are to be sold at a discount).
     * Transactions should always be active at the time of creation, i.e. a transaction cannot 
     * already be set to finalised upon instantiation.
     * @param customer the customer who the starting the transaction (beginning to shop).
     */
    public SpecialSaleTransaction(Customer customer) {
        this(customer, new HashMap<>());
    }

    /**
     * Construct a new special sale transaction for an associated customer, 
     * with a set of discounts to be applied to nominated product types on purchasing.
     * <br>
     * Transactions should always be active at the time of creation, i.e. a transaction cannot 
     * already be set to finalised upon instantiation.
     * <br>
     * Note: not all existing products must have a discount (these products will simply be sold 
     * at full price), and there is no requirement that the farm inventory currently contains 
     * any products of some type for it to be allowed a discount (these products simply cannot be 
     * purchased, so any discount specified will be ignored). 
     * @param customer the customer who is starting the transaction (beginning to shop).
     * @param discounts a mapping from product barcodes to the associated discount applied on 
     *                  purchasing, where discount amounts are specified as an integer percentage 
     *                  (e.g. for a 10% discount, the value stored is 10). 
     * @requires {@code 0 <= discount amount <= 100}
     */
    public SpecialSaleTransaction(Customer customer, Map<Barcode, Integer> discounts) {
        super(customer);
        this.discounts = discounts;
    }

    /**
     * Determines the total price for the provided product type within this transaction, with 
     * any specified discount applied as an integer percentage taken from the usual subtotal.
     * <p>
     * Note: this calculation must be performed by first determining the
     * undiscounted subtotal in full,
     * <em>then</em> calculating discounts as a percentage of this subtotal. That is, <em>do not</em>
     * calculate it by taking the discount from individual product prices and summing the result.
     * @param type the product type whose subtotal should be calculated.
     * @return the total (discounted) price for all instances of that product type within this transaction.
     */
    @Override
    public int getPurchaseSubtotal(Barcode type) {
        int subtotal = super.getPurchaseSubtotal(type);
        if (discounts.containsKey(type)) { 
            subtotal -= (int) (getDiscountAmount(type) * subtotal / 100.0);
        }
        return subtotal;
        // student version: return (int) (super.getPurchaseSubtotal(type) * (100 - getDiscountAmount(type)) / 100.0);
        
    }

    /**
     * Retrieves the discount percentage that will be applied for a particular product type, 
     * as an integer (e.g. for a 10% discount, this method should return 10).
     * <br>
     * If there is no discount percentage for that Product, returns 0.
     * @param type the product type.
     * @return the amount the product is discounted by, as an integer percentage.
     */
    public int getDiscountAmount(Barcode type) {
        return discounts.getOrDefault(type, 0);
    }

    /**
     * Calculates the total price (with discounts) of all the current products in the transaction.
     * @return the total (discounted) price calculated.
     * <p>
     * Note: this method must make use of {@link
     * SpecialSaleTransaction#getPurchaseSubtotal(Barcode)}
     * to calculate the total.
     */
    @Override
    public int getTotal() {
        if (discounts.isEmpty()) {
            return super.getTotal();
        }
        int totalWithDiscounts = 0;
        for (Barcode type : getPurchasedTypes()) {
            totalWithDiscounts += getPurchaseSubtotal(type);
        }
        return totalWithDiscounts;
    }

    /**
     * Calculates how much the customer has saved from discounts.
     * @return the numerical savings from discounts.
     */
    public int getTotalSaved() {
        return super.getTotal() - this.getTotal();
    }

    /**
     * Create entry for receipt for the given product.
     * @hidden
     */
    protected List<String> populateReceiptEntry(Barcode type) {
        List<String> receiptEntry = super.populateReceiptEntry(type);
        if (getDiscountAmount(type) > 0) {
            receiptEntry.add("Discount applied! " + getDiscountAmount(type) + "% off "
                    + type.getDisplayName());
        }
        return receiptEntry;
    }

    /**
     * Returns a string representation of this transaction and its current state. 
     * The representation contains information about the customer, the transaction's status, the associated products, and the discounts to be applied.
     * <br>
     * It is in the form {@code Transaction {Customer: <customer>, Status: <status>, Associated Products: <products>, Discounts: <discounts>}}
     * <p>
     * <b>Example output/s: </b>
     * <p>
     * <pre style="color:#00CC00">"Transaction {Customer: James Smith | Phone Number: 12345678 | Address: 1st Street, Status: Active, Associated Products: [egg: 50c *REGULAR*], Discounts: {EGG=10}}"</pre>
     * <pre style="color:#00CC00">"Transaction {Customer: Smith Ereens | Phone Number: 00011122 | Address: 2nd Street, Status: Active, Associated Products: [], Discounts: {MILK=50, JAM=0, EGG=10}}"</pre>
     * <pre style="color:#00CC00">"Transaction {Customer: Mac Donald | Phone Number: 34343434 | Address: 3rd Street, Status: Finalised, Associated Products: [milk: 440c *IRIDIUM*, milk: 440c *GOLD*], Discounts: {}}"</pre>
     * <p>
     * The status of the transaction must be either {@code Active} or {@code Finalised}.
     * The list of associated products should match that returned by {@link Transaction#getPurchases()}.
     * The list of discounts should show exactly those discounts for which entries exist in the map 
     * of discounts managed by the class, and no others. If an entry exists with a discount value of
     * 0, it should still be shown. If the map is empty, empty brackets ({}) should be shown.
     * The formatting of the discount list should match that returned by {@link AbstractMap#toString()}.
     * Any discounts should not yet be applied to the cost reported by individual products in the products list.
     * @hint See {@link Customer#toString()} and {@link Product#toString()}
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.replace(sb.lastIndexOf("}"), sb.lastIndexOf("}") + 1,
                ", Discounts: " + discounts.toString());
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts the transaction into a formatted receipt for display, using the {@link ReceiptPrinter}.
     * <p>
     * If the transaction has not been finalised, an accurate receipt cannot be printed; use
     * {@link ReceiptPrinter#createActiveReceipt()} to create an empty receipt reporting this.
     * <p>
     * Otherwise, use the {@link ReceiptPrinter#createReceipt(List, List, String, String)} and/or
     * {@link ReceiptPrinter#createReceipt(List, List, String, String, String)}. 
     * The displayed transaction must match the following requirements:
     * <ul>
     *     <li>The headings must be "Item", "Qty", "Price (ea.)", and "Subtotal", in that order.</li>
     *     <li>Each entry must be a list containing the display name of a product type, the number
     *    of products of that type purchased, the price per item for that type, and the subtotal
     *    for that type.
     *    If a non-zero discount has been applied for the entry's product type, the entry should
     *    also contain a message reporting that discount as follows:
     *     <br>
     *     "Discount applied! [discount amount]% off [product name]", where [discount amount] 
     *     is replaced with the actual percentage and [product name] is the display name of the product type.
     *     </li>
     *     <li>The entries in the list should appear in the order matching that of {@link
     *     Barcode#values()}.
     *     Product types for which no items were purchased should not be included.</li>
     *     <li><em>Iff the total savings incurred on this transaction are greater than zero,</em> 
     *     these savings should also be reported; exclude them otherwise.</li>
     *     <li>All prices shown on the receipt – that is, prices per product, subtotals, the
     *     overall total, and the savings - must be styled in standard price format rather than as
     *     cents, e.g. 157c becomes $1.57.</li>
     * </ul>
     * <p>
     * The following is an example of how the receipt should look for an example special sale transaction.
     * <p>
     * <pre>
     * {@code
     * Customer jack = new Customer("Jack", 01234567, "1st Street");
     *
     * jack.getCart().addProduct(new Egg());
     * jack.getCart().addProduct(new Milk());
     * jack.getCart().addProduct(new Jam());
     * jack.getCart().addProduct(new Egg());
     * jack.getCart().addProduct(new Milk());
     * jack.getCart().addProduct(new Egg());
     *
     * Map<Barcode, Integer> discounts = new HashMap<>();
     * discounts.put(Barcode.MILK, 50);
     * discounts.put(Barcode.JAM, 0);
     *
     * Transaction transaction = new SpecialSaleTransaction(jack, discounts);
     * transaction.finalise();
     *
     * System.out.println(transaction.getReceipt());
     * }
     * </pre>    
     * <pre style="color:#00CC00">
     * ================================================
     *                The CSSE2002 Farm
     *      Building 78, University of Queensland
     *
     *                       ╱|、
     *                     (˚ˎ 。7
     *                      |、˜〵
     *                      じしˍ,)ノ
     *
     * ================================================
     * Item       Qty       Price (ea.)       Subtotal
     * ------------------------------------------------
     * egg        3         $0.50             $1.50
     * milk       2         $4.40             $4.40
     * Discount applied! 50% off milk
     * jam        1         $6.70             $6.70
     * ------------------------------------------------
     * Total:                  $12.60
     * ------------------------------------------------
     * ***** TOTAL SAVINGS: $4.40 *****
     * ------------------------------------------------
     *      Thank you for shopping with us, Jack!
     *
     * ================================================
     * </pre>
     * @return the styled receipt representation of this transaction
     * @hint remember that sets and maps in Java <em>are not ordered.</em> If you are using either
     * of these to keep track of purchased product types (which is a perfectly good idea!),
     * you must ensure you find a way to control the order in which you retrieve things from them.
     * @hint take note of the difference in behaviour between {@link
     * ReceiptPrinter#createReceipt(List, List, String, String)} and {@link
     * ReceiptPrinter#createReceipt(List, List, String, String, String)} – in this method, both
     * versions may be helpful to you under different circumstances.
     */
    @Override
    public String getReceipt() {
        if (! this.isFinalised()) {
            return ReceiptPrinter.createActiveReceipt();
        }
        if (getTotalSaved() <= 0) {
            return super.getReceipt(); // no discounts
        }
        List<List<String>> items = new ArrayList<>(getPurchases().size());
        // item, qty, price, subtotal 
        for (Barcode type : Barcode.values()) {
            if (getPurchasedTypes().contains(type)) {
                items.add(populateReceiptEntry(type));
            }
        }
        return ReceiptPrinter.createReceipt(List.of("Item", "Qty", "Price (ea.)", "Subtotal"),
                items, getDisplayPrice(getTotal()), getAssociatedCustomer().getName(),
                getDisplayPrice(getTotalSaved()));
    }
}
