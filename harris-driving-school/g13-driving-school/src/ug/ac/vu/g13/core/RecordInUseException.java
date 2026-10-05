package ug.ac.vu.g13.core;

/**
 * Thrown when a record cannot be removed because another module still uses it.
 *
 * @author Kefas (Integration lead)
 */
public class RecordInUseException extends RuntimeException {
    public RecordInUseException(String message) {
        super(message);
    }
}
