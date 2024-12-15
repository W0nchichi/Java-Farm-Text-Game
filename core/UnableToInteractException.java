package farm.core;

/**
 * Thrown when a resource cannot be harvested.
 */
public class UnableToInteractException extends Exception {
    /**
     * Construct a failed harvest exception without any additional details.
     */
    public UnableToInteractException() {
        super();
    }

    /**
     * Construct a failed harvest exception with a message describing the exception.
     * @param message The description of the exception.
     */
    public UnableToInteractException(String message) {
        super(message);
    }
}
