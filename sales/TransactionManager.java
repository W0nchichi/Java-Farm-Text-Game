package farm.sales;

import farm.core.FailedTransactionException;
import farm.inventory.product.Product;
import farm.sales.transaction.Transaction;

/**
 * The controlling class for all transactions.
 * <p>
 * Opens and closes transactions, as well as ensuring only <b>one</b> transaction is active at any given time.
 * <p>
 * Does not create transactions but rather keeps track of the currently ongoing transaction and thus the subsequent customer cart associated with it.
 * @stage2
 */
public class TransactionManager {
    private Transaction ongoing;

    /**
     * Determine whether a transaction is currently in progress.
     * @return true iff a transaction is in progress, else false.
     */
    public boolean hasOngoingTransaction() {
        return this.ongoing != null;
    }

    /**
     * Begins managing the specified transaction, provided one is not already ongoing.
     * <p>
     * Method either sets the given transaction to be the one currently in progress, and thus
     * to be managed by subsequent calls to the transaction manager whilst the transaction remains
     * ongoing, or rejects the request if a transaction is already ongoing.
     * @param transaction the transaction to set as the manager's ongoing transaction.
     * @throws FailedTransactionException iff a transaction is already in progress.
     */
    public void setOngoingTransaction(Transaction transaction) throws FailedTransactionException {
        if (hasOngoingTransaction()) {
            throw new FailedTransactionException("Transaction already in progress.");
        }
        this.ongoing = transaction;
    }

    /**
     * Adds the given product to the cart of the customer associated with the current transaction.
     * <p>
     * The product can only be added if there is currently an ongoing transaction and that 
     * transaction has not already been finalised.
     * @param product the product to add to customer's cart.
     * @requires the provided product is known to be valid for purchase, i.e. has been successfully 
     * retrieved from the farm's inventory 
     * @throws FailedTransactionException iff there is no ongoing transaction or the transaction has already been finalised.
     */
    public void registerPendingPurchase(Product product) throws FailedTransactionException {
        if (!hasOngoingTransaction()) {
            throw new FailedTransactionException("No ongoing exception.");
        } else if (ongoing.isFinalised()) {
            throw new FailedTransactionException(
                    "Ongoing transaction has already been finalised exception.");
        }
        ongoing.getAssociatedCustomer().getCart().addProduct(product);
    }

    /**
     * Finalises the currently ongoing transaction and makes readies the TransactionManager to accept a new ongoing transaction.
     * <p>
     * The current transaction can only be closed if there is an actively ongoing transaction.
     * <p>
     * If the currently active transaction is already finalised or contains no purchases it should still be finalised and the manager readied to accept a new transaction.
     * <p>
     * When the current transaction is closed the customers cart must also be emptied in preparation for their next shop.
     * @return the finalised transaction.
     * @throws FailedTransactionException iff there is no currently ongoing transaction to close.
     */
    public Transaction closeCurrentTransaction() throws FailedTransactionException { 
        if (ongoing == null) {
            throw new FailedTransactionException("No ongoing transaction in progress.");
        }
        
        ongoing.finalise();

        Transaction result = ongoing;
        ongoing = null;
        return result; 
    }
}
