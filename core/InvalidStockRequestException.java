package farm.core;

/**
 * Thrown if attempting to perform a stock operation with invalid parameters.
 * @stage2
 */
public class InvalidStockRequestException extends Exception {
    /**
     * Construct an exception in response to an invalid stock request with no additional details.
     */
    public InvalidStockRequestException() {
        super();
    }

    /**
     * Construct an exception in response to an invalid stock request with a message 
     * describing the exception.
     * @param message The description of the exception.
     */
    public InvalidStockRequestException(String message) {
        super(message);
    }

}
