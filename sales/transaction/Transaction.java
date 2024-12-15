package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.sales.ReceiptPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transactions keeps track of what items are to be (or have been) purchased and by whom.
 * <p>
 * This is a very basic and vanilla transaction that adds things in the order they were purchased,
 * and displays them in the resulting receipt in a basic list following that order.
 * <p>
 * Transactions exists in one of two states: 
 * <ul>
 *    <li><em>Active,</em> where products can be actively added to the customer cart and the receipt is not yet ready, or</li>
 *    <li><em>Finalised,</em> where it is no longer open for modification, and instead represents a past sale that has been completed with a receipt generated.</li>
 * </ul>
 * @stage1
 */
public class Transaction {
    private final Customer customer;
    private boolean isFinalised = false;
    private List<Product> finalPurchases;

    /**
     * Construct a new transaction for an associated customer.
     * Transactions should always be active at the time of creation, i.e. a transaction cannot 
     * already be set to finalised upon instantiation.
     * @param customer the customer who is starting the transaction (beginning to shop).
     */
    public Transaction(Customer customer) {
        this.customer = customer;
    }

    /**
     * Retrieves the customer associated with this transaction.
     * @return the customer of the transaction.
     * @hint a customer has a cart; the transaction does not.
     */
    public Customer getAssociatedCustomer() {
        return this.customer;
    }
    
    /**
     * Retrieves all products associated with the transaction.
     * <br>
     * If the transaction has been finalised, this is all products that were 'locked in' as 
     * final purchases at that time. If the transaction is instead still active, it is all products 
     * currently in the associated customer's cart.
     * @return the list of purchases comprising the transaction. 
     * @ensures the returned list is a shallow copy and cannot modify the original transaction
     * @hint once a customer has finished shopping, they can do whatever they like with their cart,
     * and may even come back with it another day; you should not expect that it continues to contain their final purchases.
     */
    public List<Product> getPurchases() {
        if (isFinalised) {
            return this.finalPurchases;
        } 
        return customer.getCart().getContents();
    }

    /**
     * Calculates the total price of all the current products in the transaction.
     * @return the total price calculated.
     */
    public int getTotal() {
        int total = 0;
        for (Product product : getPurchases()) {
            total += product.getBasePrice();
        }
        return total;
    }

    /**
     * Determines if the transaction is finalised (i{@literal .}e{@literal .} sale completed) or not.
     * @return true iff the transaction is over, else false.
     */
    public boolean isFinalised() {
        return isFinalised;
    }

    /**
     * Mark a transaction as finalised and update the transaction's internal state accordingly.
     * <br>
     * This locks in all pending purchases previously added, such that they are now treated as 
     * final purchases and no additional modification can be made, and empties the customer's cart.
     */
    public void finalise() {
        if (!isFinalised) {
            finalPurchases = Collections.unmodifiableList(customer.getCart().getContents());
            getAssociatedCustomer().getCart().setEmpty();
            this.isFinalised = true;
        }
    }


    /**
     * Returns a string representation of this transaction and its current state. 
     * The representation contains information about the customer, the transaction's status, and the associated products.
     * <br>
     * It is in the form {@code Transaction {Customer: <customer>, Status: <status>, Associated Products: <products>}}
     * <p>
     * <b>Example output/s: </b>
     * <p>
     * <pre style="color:#00CC00">"Transaction {Customer: James Smith | Phone Number: 12345678 | Address: 1st Street, Status: Active, Associated Products: [egg: 50c *REGULAR*]}"</pre>
     * <pre style="color:#00CC00">"Transaction {Customer: Lauren Jame | Phone Number: 80145689 | Address: 2nd Street, Status: Finalised, Associated Products: []}"</pre>
     * <pre style="color:#00CC00">"Transaction {Customer: Alfred Bruce | Phone Number: 01010101 | Address: 3rd Street, Status: Finalised, Associated Products: [milk: 440c *IRIDIUM*, milk: 440c *GOLD*]}"</pre>
     * <p>
     * The status of the transaction must be either {@code Active} or {@code Finalised}.
     * The list of associated products should match that returned by {@link Transaction#getPurchases()}.
     * @hint See {@link Customer#toString()} and {@link Product#toString()}
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("Transaction {"));
        sb.append("Customer: ").append(customer.toString().substring("Name: ".length()));
        sb.append(", ").append("Status: ");
        if (isFinalised) {
            sb.append("Finalised");
        } else {
            sb.append("Active");
        }
        sb.append(", ").append("Associated Products: ");
        sb.append(getPurchases().toString()).append("}");
        return sb.toString();
    }

    /**
     * Converts the transaction into a formatted receipt for display, using the {@link ReceiptPrinter}.
     * <p>
     * If the transaction has not been finalised, an accurate receipt cannot be printed; use
     * {@link ReceiptPrinter#createActiveReceipt()} to create an empty receipt reporting this.
     * <p>
     * Otherwise, use the {@link ReceiptPrinter#createReceipt(List, List, String, String)}. 
     * The displayed transaction must match the following requirements:
     * <ul>
     *     <li>The headings must be "Item" and "Price", in that order.</li>
     *     <li>Each entry must be a list containing the display name and price of a purchased product.</li>
     *     <li>The list of entries should be ordered such that products appear in the order they were purchased.</li>
     *     <li>All prices shown on the receipt – that is, both prices per product and the overall
     *     total - must be styled in standard price format rather than as cents,
     *     e.g. 157c becomes $1.57.</li>
     * </ul>
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
     * Transaction transaction = new Transaction(jack);
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
     * Item                                       Price
     * ------------------------------------------------
     * egg                                        $0.50
     * milk                                       $4.40
     * jam                                        $6.70
     * egg                                        $0.50
     * milk                                       $4.40
     * egg                                        $0.50
     * ------------------------------------------------
     * Total:                  $17.00
     * ------------------------------------------------
     *      Thank you for shopping with us, Jack!
     *
     * ================================================
     * </pre>
     * @return the styled receipt representation of this transaction
     */
    public String getReceipt() {
        if (!isFinalised) {
            return ReceiptPrinter.createActiveReceipt();
        }

        List<List<String>> items = new ArrayList<>(finalPurchases.size());
        for (Product product : finalPurchases) {
            items.add(List.of(product.getDisplayName(), getDisplayPrice(product.getBasePrice())));
        }
        return ReceiptPrinter.createReceipt(List.of("Item", "Price"),
                items, getDisplayPrice(getTotal()), getAssociatedCustomer().getName());
    }

    /**
     * Converts cents into their string representation of dollars.
     * @hidden Custom helper method
     * @param cents amount of cents to covert
     * @return the converted dollars as a string.
     */
    protected String getDisplayPrice(int cents) {
        String price = Integer.toString(cents);
        while (price.length() < 3) {
            price = "0" + price;
        }
        return "$" + price.substring(0, price.length() - 2) + "."
                + price.substring(price.length() - 2);

    }
}
