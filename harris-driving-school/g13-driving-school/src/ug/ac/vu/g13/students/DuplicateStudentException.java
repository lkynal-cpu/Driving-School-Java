package ug.ac.vu.g13.students;

/**
 * Thrown when a student with the same name and date of birth is already registered.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(Student existing) {
        super("This student seems to be registered already as " + existing.getId() + " (" + existing.getFullName() + ").");
    }
}
