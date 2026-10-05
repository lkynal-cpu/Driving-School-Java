package ug.ac.vu.g13.core;

/**
 * Thrown by constructors and setters when a value is not acceptable.
 *
 * @author Kefas (Integration lead)
 */
public class InvalidDataException extends IllegalArgumentException {
    public InvalidDataException(String message) {
        super(message);
    }
}
