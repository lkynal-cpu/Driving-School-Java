package ug.ac.vu.g13.courses;

import java.util.ArrayList;
import java.util.List;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.Reportable;
import ug.ac.vu.g13.core.VehicleClass;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.students.StudentService;

/**
 * Submenu for the Courses module.
 *
 * @author Member 4 (Courses module owner)
 */
public class CoursesMenu extends AbstractModuleMenu {
    private final CourseService courses;
    private final EnrollmentService enrollments;
    private final StudentService students;

    public CoursesMenu(CourseService courses, EnrollmentService enrollments, StudentService students) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.students = students;
    }

    @Override
    public String getName() {
        return "Courses and Enrolment";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Add a course", "List courses", "Update a course", "Remove a course",
            "Enrol a student on a course", "List enrolments", "Change enrolment status",
            "Report: courses / enrolments (sorted / filtered)", "Overview of courses and enrolments"
        };
    }

    @Override
    protected void handle(int choice) throws Exception {
        switch (choice) {
            case 1: addCourse(); break;
            case 2: Menu.printList("Courses", courses.sorted(CourseService.BY_NAME)); break;
            case 3: updateCourse(); break;
            case 4: removeCourse(); break;
            case 5: enrol(); break;
            case 6: Menu.printList("Enrolments", enrollments.sorted(EnrollmentService.BY_COURSE_THEN_STUDENT)); break;
            case 7: changeStatus(); break;
            case 8: report(); break;
            case 9: overview(); break;
            default: break;
        }
    }

    private void addCourse() {
        String name = InputHelper.readRequired("Course name (e.g. Car Basic Course): ");
        VehicleClass vc = InputHelper.readEnum("Vehicle class: ", VehicleClass.values());
        int weeks = InputHelper.readInt("Duration in weeks (1-52): ", 1, 52);
        int hours = InputHelper.readInt("Practical hours (1-200): ", 1, 200);
        double fee = InputHelper.readDouble("Fee in UGX: ", 1000, 10_000_000);
        int cap = InputHelper.readInt("Maximum learners (1-60): ", 1, 60);
        Course c = courses.create(name, vc, weeks, hours, fee, cap);
        Menu.ok("Course " + c.getName() + " added with ID " + c.getId());
    }

    private void updateCourse() {
        String id = InputHelper.readRequired("Course ID (e.g. G13-C001): ");
        courses.require(id);
        int f = InputHelper.readChoice("Change: ", "Fee", "Capacity", "Duration in weeks", "Practical hours", "Name");
        if (f == 0) {
            double v = InputHelper.readDouble("New fee in UGX: ", 1000, 10_000_000);
            courses.update(id, c -> c.setFee(v));
        } else if (f == 1) {
            int v = InputHelper.readInt("New capacity: ", 1, 60);
            courses.update(id, c -> c.setCapacity(v));
        } else if (f == 2) {
            int v = InputHelper.readInt("New duration in weeks: ", 1, 52);
            courses.update(id, c -> c.setDurationWeeks(v));
        } else if (f == 3) {
            int v = InputHelper.readInt("New practical hours: ", 1, 200);
            courses.update(id, c -> c.setPracticalHours(v));
        } else {
            String v = InputHelper.readRequired("New name: ");
            courses.update(id, c -> c.setName(v));
        }
        Menu.ok("Course updated.");
    }

    private void removeCourse() {
        String id = InputHelper.readRequired("Course ID to remove: ");
        Course c = courses.require(id);
        if (InputHelper.readYesNo("Remove " + c.getName() + "?")) {
            courses.remove(id);
            Menu.ok("Course removed.");
        }
    }

    private void enrol() throws CourseFullException {
        Student s = students.require(InputHelper.readRequired("Student ID: "));
        Course c = courses.require(InputHelper.readRequired("Course ID: "));
        Enrollment e = enrollments.enroll(s, c);
        Menu.ok("Enrolled " + s.getFullName() + " on " + c.getName() + " (" + e.getId() + ").");
    }

    private void changeStatus() {
        String id = InputHelper.readRequired("Enrolment ID (e.g. G13-E001): ");
        enrollments.require(id);
        Enrollment.Status st = InputHelper.readEnum("New status: ", Enrollment.Status.values());
        enrollments.update(id, e -> e.setStatus(st));
        Menu.ok("Enrolment updated.");
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "Courses cheapest first", "Enrolments by course and student",
                "Enrolments with one status", "Seats left on each course");
        if (how == 0) {
            Menu.printList("Courses by fee", courses.sorted(CourseService.BY_FEE));
        } else if (how == 1) {
            Menu.printList("Enrolments by course", enrollments.sorted(EnrollmentService.BY_COURSE_THEN_STUDENT));
        } else if (how == 2) {
            Enrollment.Status st = InputHelper.readEnum("Status: ", Enrollment.Status.values());
            List<Enrollment> list = enrollments.filter(e -> e.getStatus() == st);
            list.sort(EnrollmentService.BY_COURSE_THEN_STUDENT);
            Menu.printList("Enrolments: " + st, list);
        } else {
            System.out.println("--- Seats left ---");
            for (Course c : courses.sorted(CourseService.BY_NAME)) {
                System.out.println("  " + c.getName() + ": " + (c.getCapacity() - enrollments.activeCount(c))
                        + " of " + c.getCapacity() + " seats free");
            }
        }
    }

    /** Polymorphism: one loop over courses and enrolments through Reportable. */
    private void overview() {
        List<Reportable> mixed = new ArrayList<>(courses.sorted(CourseService.BY_NAME));
        mixed.addAll(enrollments.sorted(EnrollmentService.BY_COURSE_THEN_STUDENT));
        System.out.println("--- Courses and enrolments (" + mixed.size() + ") ---");
        for (Reportable r : mixed) {
            System.out.println("  " + r.reportLine());
        }
    }
}
