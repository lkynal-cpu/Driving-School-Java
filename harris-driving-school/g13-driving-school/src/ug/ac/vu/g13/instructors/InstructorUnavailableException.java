package ug.ac.vu.g13.instructors;

/**
 * Thrown when an instructor is inactive or not on shift at the requested time.
 *
 * @author Member 2 (Instructors module owner)
 */
public class InstructorUnavailableException extends Exception {
    public InstructorUnavailableException(String message) {
        super(message);
    }
}
