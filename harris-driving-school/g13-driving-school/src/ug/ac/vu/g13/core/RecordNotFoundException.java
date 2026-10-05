package ug.ac.vu.g13.core;

/**
 * Thrown when a record with the given ID does not exist.
 *
 * @author Kefas (Integration lead)
 */
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String what, String id) {
        super(what + " '" + id + "' was not found.");
    }
}
