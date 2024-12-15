package farm.core;

/**
 * Thrown if a transaction failed.
 * @stage2
 */
public class FailedTransactionException extends Exception {
    /**
     * Construct a failed transaction exception without any additional details.
     */
    public FailedTransactionException() {
        super();
    }
    
    /**
     * Construct a failed transaction exception with a message describing the exception. 
     * @param message The description of the exception.
     */
    public FailedTransactionException(String message) {
        super(message);
    }
}
