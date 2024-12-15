package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.sales.ReceiptPrinter;

import java.util.*;


/**
 * A transaction type that allows products to be categorised by their types, not solely as 
 * isolated individual products. The resulting receipt therefore displays purchased types with
 * an associated quantity purchased and subtotal, rather than a single line for each product. 
 * @stage1
 */
public class CategorisedTransaction extends Transaction {
    /**
     * Construct a new categorised transaction for an associated customer.
     * Transactions should always be active at the time of creation, i.e. a transaction cannot 
     * already be set to finalised upon instantiation.
     * @param customer the customer who is starting the transaction (beginning to shop).
     */
    public CategorisedTransaction(Customer customer) {
        super(customer);
    }

    /**
     * Retrieves all unique product types of the purchases associated with the transaction.
     * @return a set of all product types in the transaction.
     */
    public Set<Barcode> getPurchasedTypes() {
        return this.getPurchasesByType().keySet();
    }

    /**
     * Retrieves all products associated with the transaction, grouped by their type.
     * <br>
     * If the transaction has been finalised, this is all products that were 'locked in' as 
     * final purchases at that time. If the transaction is instead still active, it is all products 
     * currently in the associated customer's cart.
     * @return the products in the transaction, grouped by their type.
     */
    public Map<Barcode, List<Product>> getPurchasesByType() {
        Map<Barcode, List<Product>> purchasesByType = new HashMap<>();
        for (Product purchase : getPurchases()) {
            List<Product> purchases = 
                    purchasesByType.getOrDefault(purchase.getBarcode(), new ArrayList<>());
            purchases.add(purchase);
            purchasesByType.putIfAbsent(purchase.getBarcode(), purchases);
        }
        return purchasesByType;
    }

    /**
     * Retrieves the number of products of a particular type associated with the transaction.
     * @param type the product type.
     * @return the number of products of the specified type associated with the transaction.
     */
    public int getPurchaseQuantity(Barcode type) {
        return getPurchasesByType().getOrDefault(type, Collections.emptyList()).size();
    }

    /**
     * Determines the total price for the provided product type within this transaction.
     * @param type the product type.
     * @return the total price for all instances of that product type within the transaction, or
     * 0 if no items of that type are associated with the transaction.
     */
    public int getPurchaseSubtotal(Barcode type) {
        int subtotal = 0;
        List<Product> purchased = getPurchasesByType().getOrDefault(type, Collections.emptyList());
        for (Product product : purchased) {
            subtotal += product.getBasePrice();
        }
        return subtotal;
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
     *     <li>The headings must be "Item", "Qty", "Price (ea.)", and "Subtotal", in that order.</li>
     *     <li>Each entry must be a list containing the identifier of a product type, the number of 
     *     products of that type purchased, the price per item for that type, and the subtotal for that type.</li>
     *     <li>The entries in the list should appear in the order matching that of {@link
     *     Barcode#values()}. Product types for which no items were purchased should not be
     *     included.</li>
     *     <li>All prices shown on the receipt – that is, both prices per product and the
     *     overall total - must be styled in standard price format rather
     *     than as cents, e.g. 157c becomes $1.57.</li>
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
     * Transaction transaction = new CategorisedTransaction(jack);
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
     * Item       Qty       Price (ea.)      Subtotal
     * ------------------------------------------------
     * egg       3         $0.50            $1.50
     * milk      2         $4.40            $8.80
     * jam       1         $6.70            $6.70
     * ------------------------------------------------
     * Total:                  $17.00
     * ------------------------------------------------
     *      Thank you for shopping with us, Jack!
     *
     * ================================================
     * </pre>
     * @return the styled receipt representation of this transaction
     * @hint remember that sets and maps in Java <em>are not ordered.</em>
     * If you are using either of these to keep track of purchased product types (which is a
     * perfectly good idea!), you must ensure you find a way to control the order in which you
     * retrieve things from them.
     */
    @Override
    public String getReceipt() {
        if (! this.isFinalised()) {
            return ReceiptPrinter.createActiveReceipt();
        }

        List<List<String>> items = new ArrayList<>(getPurchases().size());
        // item, qty, price, subtotal 
        for (Barcode type : Barcode.values()) {
            if (getPurchasedTypes().contains(type)) {
                items.add(populateReceiptEntry(type));
            }
        }
        return ReceiptPrinter.createReceipt(List.of("Item", "Qty", "Price (ea.)", "Subtotal"),
                items, getDisplayPrice(getTotal()), getAssociatedCustomer().getName());
    }

    /**
     * Create entry for receipt for the given product.
     * @hidden Custom private method for helping with receipt preparation
     */
    protected List<String> populateReceiptEntry(Barcode type) {
        List<String> receiptEntry = new ArrayList<>();
        receiptEntry.add(type.getDisplayName());
        receiptEntry.add(String.valueOf(getPurchaseQuantity(type)));
        receiptEntry.add(getDisplayPrice(type.getBasePrice()));
        receiptEntry.add(getDisplayPrice(getPurchaseSubtotal(type)));
        return receiptEntry;
    }
}
