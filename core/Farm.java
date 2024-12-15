package farm.core;

import farm.customer.AddressBook;
import farm.customer.Customer;
import farm.inventory.Inventory;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.sales.TransactionHistory;
import farm.sales.TransactionManager;
import farm.sales.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level model class responsible for storing and making updates to the data and
 * smaller model entities that make up the internal state of a farm.
 */
public class Farm {
    private final Inventory inventory;
    private final AddressBook addressBook;
    private final TransactionManager transactionManager;
    private final TransactionHistory history;

    /**
     * Creates a new farm instance with an inventory and address book supplied.
     * @param inventory The inventory through which access to the farm's stock is provisioned.
     * @param addressBook The address book storing the farm's customer records.
     */
    public Farm(Inventory inventory, AddressBook addressBook) {
        this.inventory = inventory;
        this.addressBook = addressBook;
        this.transactionManager = new TransactionManager();
        this.history = new TransactionHistory();
    }

    /**
     * Retrieves all customer records currently stored in the farm's address book.
     * @return a list of all customers in the address book
     * @ensures the returned list is a shallow copy and cannot modify the original address book
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(addressBook.getAllRecords());
    }

    /**
     * Retrieves all products currently stored in the farm's inventory.
     * @return a list of all products in the inventory
     * @ensures the returned list is a shallow copy and cannot modify the original inventory
     */
    public List<Product> getAllStock() {
        return new ArrayList<>(inventory.getAllProducts());
    }

    /**
     * Retrieves the farm's transaction manager.
     * @return the farm's transaction manager
     */
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /**
     * Retrieves the farm's transaction history.
     * @return the farm's transaction history
     */
    public TransactionHistory getTransactionHistory() {
        return this.history;
    }

    /**
     * Saves the supplied customer in the farm's address book.
     * @param customer The customer to add to the address book.
     * @throws DuplicateCustomerException if the address book already contains this customer
     */
    public void saveCustomer(Customer customer) throws DuplicateCustomerException {
        addressBook.addCustomer(customer);
    }

    /**
     * Adds a single product of the specified type and quality to the farm's inventory.
     * @param barcode the product type to add to the inventory.
     * @param quality the quality of the product to add to the inventory.
     */
    public void stockProduct(Barcode barcode, Quality quality) {
        inventory.addProduct(barcode, quality);
    }
    
    /**
     * Adds some quantity of products of the specified type and quality to the farm's inventory.
     * @param barcode the product type to add to the inventory.
     * @param quality the quality of the product to add to the inventory.
     * @param quantity the number of products to add to the inventory.
     * @throws IllegalArgumentException if a negative quantity is provided.
     * @throws InvalidStockRequestException if the quantity is greater than 1 when a
     * FancyInventory is not in use.
     */
    public void stockProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        if (quantity <= 0) {
            // Early exit under exceptional conditions
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        if (quantity == 1) {
            inventory.addProduct(barcode, quality);
        } else {
            inventory.addProduct(barcode, quality, quantity);
        }
    }

    /**
     * Sets the provided transaction as the current ongoing transaction.
     * @param transaction the transaction to set as ongoing.
     * @requires the customer associated with transaction exists in the farm's addressbook.
     * @throws FailedTransactionException if the farm's transaction manager rejects the request
     * to begin managing this transaction.
     */
    public void startTransaction(Transaction transaction) throws FailedTransactionException {
        transactionManager.setOngoingTransaction(transaction);
    }

    /**
     * Attempts to add a single product of the given type to the customer's shopping cart.
     * @param barcode the product type to add.
     * @return the number of products successfully added to the cart.
     * i.e. if no products of this type exist in the inventory, this method will return 0.
     * @throws FailedTransactionException if no transaction is ongoing.
     */
    public int addToCart(Barcode barcode) throws FailedTransactionException {
        return addToCart(barcode, 1);
    }

    /**
     * Attempts to add the specified number of products of the given type to the customer's shopping
     * cart.
     * @param barcode the product type to add.
     * @param quantity the number of products to add.
     * @return the number of products successfully added to the cart.
     * @throws FailedTransactionException if no transaction is ongoing, or if the quantity is
     * greater than 1 when a FancyInventory is not in use.
     * @throws IllegalArgumentException if a quantity less than 1 is entered.
     */
    public int addToCart(Barcode barcode, int quantity) throws FailedTransactionException {
        if (!transactionManager.hasOngoingTransaction()) {
            throw new FailedTransactionException(
                    "Cannot add to cart when no customer has started shopping.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        
        List<Product> products;
        if (quantity == 1) {
            products = inventory.removeProduct(barcode);
        } else {
            products = inventory.removeProduct(barcode, quantity);
        }
        
        for (Product product : products) {
            transactionManager.registerPendingPurchase(product);
        }

        return products.size();
    }

    /**
     * Closes the ongoing transaction. If items have been purchased in this transaction, records the transaction in the farm's history.
     * @return true iff the finalised transaction contained products.
     * @throws FailedTransactionException if transaction cannot be closed.
     */
    public boolean checkout() throws FailedTransactionException {
        Transaction result = transactionManager.closeCurrentTransaction();
        if (!result.getPurchases().isEmpty()) {
            history.recordTransaction(result);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves the receipt associated with the most recent transaction.
     * @return the receipt associated with the most recent transaction.
     */
    public String getLastReceipt() {
        return history.getLastTransaction().getReceipt();
    }

    /**
     * Retrieves a customer from the address book.
     * @param name the name of the customer.
     * @param phoneNumber the phone number of the customer.
     * @return the customer instance matching the name and phone number.
     * @throws CustomerNotFoundException if the customer does not exist in the address book.
     */
    public Customer getCustomer(String name, int phoneNumber) throws CustomerNotFoundException {
        return this.addressBook.getCustomer(name, phoneNumber);
    }
}
