package ug.ac.vu.g13.courses;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.students.Student;

/**
 * A student's place on a course. Links the Students and Courses modules by object reference.
 *
 * @author Member 4 (Courses module owner)
 */
public class Enrollment extends BaseRecord {
    public enum Status { ACTIVE, COMPLETED, WITHDRAWN }

    private final Student student;
    private final Course course;
    private final LocalDate enrolledOn;
    private Status status;

    public Enrollment(String id, Student student, Course course, LocalDate enrolledOn, Status status) {
        super(id);
        this.student = Validate.notNull(student, "Student");
        this.course = Validate.notNull(course, "Course");
        this.enrolledOn = Validate.date(enrolledOn, "Enrolment date", LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
        setStatus(status);
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDate getEnrolledOn() {
        return enrolledOn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Validate.notNull(status, "Status");
    }

    @Override
    public String describe() {
        return String.format("%s | Enrolment | %s (%s) -> %s | since %s | %s",
                getId(), student.getFullName(), student.getId(), course.getName(), enrolledOn, status);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), student.getId(), course.getId(), enrolledOn, status.name());
    }
}
