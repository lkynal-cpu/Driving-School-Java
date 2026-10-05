package ug.ac.vu.g13.courses;

import java.time.LocalDate;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.students.StudentService;

/**
 * Manages enrolments of students onto courses.
 *
 * @author Member 4 (Courses module owner)
 */
public class EnrollmentService extends BaseService<Enrollment> {
    public static final Comparator<Enrollment> BY_COURSE_THEN_STUDENT = Comparator
            .comparing((Enrollment e) -> e.getCourse().getName().toLowerCase())
            .thenComparing(e -> e.getStudent().getFullName().toLowerCase());

    private final StudentService students;
    private final CourseService courses;

    public EnrollmentService(StudentService students, CourseService courses) {
        super("enrollments.txt", "Enrolment");
        this.students = students;
        this.courses = courses;
        load();
    }

    @Override
    protected Enrollment fromFileLine(String line) {
        String[] f = Fields.split(line, 5);
        return new Enrollment(f[0], students.require(f[1]), courses.require(f[2]), LocalDate.parse(f[3]),
                Enrollment.Status.valueOf(f[4]));
    }

    public int activeCount(Course course) {
        return filter(e -> e.getCourse().equals(course) && e.getStatus() == Enrollment.Status.ACTIVE).size();
    }

    public Enrollment enroll(Student student, Course course) throws CourseFullException {
        if (anyMatch(e -> e.getStudent().equals(student) && e.getCourse().equals(course)
                && e.getStatus() == Enrollment.Status.ACTIVE)) {
            throw new InvalidDataException(student.getFullName() + " is already enrolled on " + course.getName() + ".");
        }
        if (activeCount(course) >= course.getCapacity()) {
            throw new CourseFullException(course);
        }
        Enrollment e = new Enrollment(nextId('E'), student, course, LocalDate.now(), Enrollment.Status.ACTIVE);
        add(e);
        return e;
    }

    /** True if the student has an ACTIVE enrolment on a course for this vehicle class. */
    public boolean hasActiveFor(Student student, VehicleClass vc) {
        return anyMatch(e -> e.getStudent().equals(student) && e.getStatus() == Enrollment.Status.ACTIVE
                && e.getCourse().getVehicleClass() == vc);
    }

    public boolean hasActive(Student student) {
        return anyMatch(e -> e.getStudent().equals(student) && e.getStatus() == Enrollment.Status.ACTIVE);
    }
}
