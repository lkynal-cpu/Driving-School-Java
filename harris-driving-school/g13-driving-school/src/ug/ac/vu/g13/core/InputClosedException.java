package ug.ac.vu.g13.core;

/**
 * Thrown when the input stream ends (for example Ctrl+D). The main program
 * catches it and exits cleanly instead of crashing.
 *
 * @author Kefas (Integration lead)
 */
public class InputClosedException extends RuntimeException {
    public InputClosedException() {
        super("Input closed.");
    }
}
