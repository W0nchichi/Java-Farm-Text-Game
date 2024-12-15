package farm.core;

/**
 * Thrown if a customer is not found when looked up.
 * @stage1
 */
public class CustomerNotFoundException extends Exception {
    /**
     * Construct a customer not found exception without any additional details.
     */
    public CustomerNotFoundException() {
        super();
    }

    /**
     * Construct a customer not found exception with a message describing the exception.
     * @param message The description of the exception.
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
