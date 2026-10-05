package ug.ac.vu.g13.tests;

/**
 * Thrown when a student does not meet the rules for booking a test.
 *
 * @author Member 7 (Tests module owner)
 */
public class NotEligibleForTestException extends Exception {
    public NotEligibleForTestException(String reason) {
        super("Not eligible for this test: " + reason);
    }
}
