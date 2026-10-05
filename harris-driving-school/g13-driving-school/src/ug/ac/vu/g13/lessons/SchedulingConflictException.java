package ug.ac.vu.g13.lessons;

/**
 * Thrown when the student, instructor or vehicle is already busy at the requested time.
 *
 * @author Member 5 (Lessons module owner)
 */
public class SchedulingConflictException extends Exception {
    public SchedulingConflictException(String who, Lesson other) {
        super(who + " already has lesson " + other.getId() + " on " + other.getDate() + " at "
                + other.getStart() + "-" + other.getEnd() + ".");
    }
}
