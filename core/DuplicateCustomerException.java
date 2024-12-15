package farm.core;

/**
 * Thrown if when attempting to add a customer, there is already an existing customer with those details.
 * @stage1
 */
public class DuplicateCustomerException extends Exception {
    /**
     * Construct a duplicate customer exception without any additional details.
     */
    public DuplicateCustomerException() {
        super();
    }

    /**
     * Construct a duplicate customer exception with a message describing the exception. 
     * @param message The description of the exception.
     */
    public DuplicateCustomerException(String message) {
        super(message);
    }
}
