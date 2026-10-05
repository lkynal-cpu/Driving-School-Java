package ug.ac.vu.g13.app;

import java.util.ArrayList;
import java.util.List;

import ug.ac.vu.g13.core.Config;
import ug.ac.vu.g13.core.InputClosedException;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.MenuModule;
import ug.ac.vu.g13.courses.CourseService;
import ug.ac.vu.g13.courses.CoursesMenu;
import ug.ac.vu.g13.courses.EnrollmentService;
import ug.ac.vu.g13.fees.FeesMenu;
import ug.ac.vu.g13.fees.InvoiceService;
import ug.ac.vu.g13.fees.PaymentService;
import ug.ac.vu.g13.instructors.InstructorService;
import ug.ac.vu.g13.instructors.InstructorsMenu;
import ug.ac.vu.g13.instructors.ShiftService;
import ug.ac.vu.g13.lessons.LessonService;
import ug.ac.vu.g13.lessons.LessonsMenu;
import ug.ac.vu.g13.maintenance.FuelService;
import ug.ac.vu.g13.maintenance.MaintenanceMenu;
import ug.ac.vu.g13.maintenance.MaintenanceService;
import ug.ac.vu.g13.students.PermitService;
import ug.ac.vu.g13.students.StudentService;
import ug.ac.vu.g13.students.StudentsMenu;
import ug.ac.vu.g13.tests.TestBookingService;
import ug.ac.vu.g13.tests.TestResultService;
import ug.ac.vu.g13.tests.TestsMenu;
import ug.ac.vu.g13.vehicles.VehicleService;
import ug.ac.vu.g13.vehicles.VehiclesMenu;

/**
 * Starts the Driving School Management System: loads every module's data,
 * connects the modules together and shows the main menu.
 *
 * @author Kefas (Integration lead)
 */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println(" " + Config.SCHOOL_NAME + " (Group " + Config.GROUP + ")");
        System.out.println("==========================================");

        // Load order matters: a module is loaded after the modules its records refer to.
        StudentService students = new StudentService();
        PermitService permits = new PermitService(students);
        InstructorService instructors = new InstructorService();
        ShiftService shifts = new ShiftService(instructors);
        VehicleService vehicles = new VehicleService();
        CourseService courses = new CourseService();
        EnrollmentService enrollments = new EnrollmentService(students, courses);
        LessonService lessons = new LessonService(students, instructors, vehicles, shifts, enrollments);
        InvoiceService invoices = new InvoiceService(enrollments);
        PaymentService payments = new PaymentService(invoices);
        TestBookingService bookings = new TestBookingService(students, enrollments, invoices);
        TestResultService results = new TestResultService(bookings);
        bookings.linkResults(results);
        MaintenanceService jobs = new MaintenanceService(vehicles);
        FuelService fuel = new FuelService(vehicles);

        wireRemovalGuards(students, permits, instructors, shifts, vehicles, courses, enrollments, lessons,
                invoices, payments, bookings, jobs, fuel);

        List<MenuModule> modules = new ArrayList<>();
        modules.add(new StudentsMenu(students, permits));
        modules.add(new InstructorsMenu(instructors, shifts));
        modules.add(new VehiclesMenu(vehicles));
        modules.add(new CoursesMenu(courses, enrollments, students));
        modules.add(new LessonsMenu(lessons, students, instructors, vehicles));
        modules.add(new FeesMenu(invoices, payments, enrollments));
        modules.add(new TestsMenu(bookings, results, students));
        modules.add(new MaintenanceMenu(jobs, fuel, vehicles));

        try {
            runMainMenu(modules);
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input closed. All changes were already saved.");
        }
        System.out.println("Goodbye.");
    }

    private static void runMainMenu(List<MenuModule> modules) {
        String[] names = new String[modules.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = modules.get(i).getName();
        }
        while (true) {
            int choice = Menu.choose("MAIN MENU", names);
            if (choice == 0) {
                return;
            }
            modules.get(choice - 1).run();
        }
    }

    /** Stops a record being removed while another module still refers to it. */
    private static void wireRemovalGuards(StudentService students, PermitService permits, InstructorService instructors,
                                          ShiftService shifts, VehicleService vehicles, CourseService courses,
                                          EnrollmentService enrollments, LessonService lessons, InvoiceService invoices,
                                          PaymentService payments, TestBookingService bookings,
                                          MaintenanceService jobs, FuelService fuel) {
        students.addRemovalGuard(s -> {
            if (permits.anyMatch(p -> p.getStudent().equals(s))) {
                return "the student has permit applications";
            }
            if (enrollments.anyMatch(e -> e.getStudent().equals(s))) {
                return "the student has enrolments";
            }
            if (lessons.anyMatch(l -> l.getStudent().equals(s))) {
                return "the student has lessons";
            }
            if (bookings.anyMatch(b -> b.getStudent().equals(s))) {
                return "the student has test bookings";
            }
            return null;
        });
        instructors.addRemovalGuard(i -> {
            if (shifts.anyMatch(s -> s.getInstructor().equals(i))) {
                return "the instructor still has shifts (remove them first)";
            }
            if (lessons.anyMatch(l -> l.getInstructor().equals(i))) {
                return "the instructor has lessons";
            }
            return null;
        });
        vehicles.addRemovalGuard(v -> {
            if (lessons.anyMatch(l -> l instanceof ug.ac.vu.g13.lessons.PracticalLesson
                    && ((ug.ac.vu.g13.lessons.PracticalLesson) l).getVehicle().equals(v))) {
                return "the vehicle is used in lessons";
            }
            if (jobs.anyMatch(j -> j.getVehicle().equals(v))) {
                return "the vehicle has maintenance jobs";
            }
            if (fuel.anyMatch(f -> f.getVehicle().equals(v))) {
                return "the vehicle has fuel logs";
            }
            return null;
        });
        courses.addRemovalGuard(c -> enrollments.anyMatch(e -> e.getCourse().equals(c))
                ? "students are enrolled on the course" : null);
        enrollments.addRemovalGuard(e -> invoices.anyMatch(i -> i.getEnrollment().equals(e))
                ? "an invoice exists for the enrolment" : null);
        invoices.addRemovalGuard(i -> payments.anyMatch(p -> p.getInvoice().equals(i))
                ? "payments exist (reverse them first)" : null);
    }
}
