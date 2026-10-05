package ug.ac.vu.g13.courses;

/**
 * Thrown when a course already has as many active learners as its capacity.
 *
 * @author Member 4 (Courses module owner)
 */
public class CourseFullException extends Exception {
    public CourseFullException(Course course) {
        super("Course '" + course.getName() + "' is full (" + course.getCapacity() + " learners).");
    }
}
