package ug.ac.vu.g13.tests;

import java.time.LocalDate;
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
 * Submenu for the Driving Tests module.
 *
 * @author Member 7 (Tests module owner)
 */
public class TestsMenu extends AbstractModuleMenu {
    private final TestBookingService bookings;
    private final TestResultService results;
    private final StudentService students;

    public TestsMenu(TestBookingService bookings, TestResultService results, StudentService students) {
        this.bookings = bookings;
        this.results = results;
        this.students = students;
    }

    @Override
    public String getName() {
        return "Driving Tests";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Book a test", "List test bookings", "Cancel a booking", "Remove a booking",
            "Record a test result", "Correct a result score", "List test results",
            "Report: results (sorted / filtered)", "Overview of bookings and results"
        };
    }

    @Override
    protected void handle(int choice) throws Exception {
        switch (choice) {
            case 1: book(); break;
            case 2: Menu.printList("Test bookings", bookings.sorted(TestBookingService.BY_DATE)); break;
            case 3: bookings.cancel(InputHelper.readRequired("Booking ID (e.g. G13-T001): ")); Menu.ok("Booking cancelled."); break;
            case 4: removeBooking(); break;
            case 5: record(); break;
            case 6: correct(); break;
            case 7: Menu.printList("Test results", results.sorted(TestResultService.BY_SCORE_DESC)); break;
            case 8: report(); break;
            case 9: overview(); break;
            default: break;
        }
    }

    private void book() throws NotEligibleForTestException {
        Student s = students.require(InputHelper.readRequired("Student ID: "));
        TestBooking.Type type = InputHelper.readEnum("Test type: ", TestBooking.Type.values());
        VehicleClass vc = InputHelper.readEnum("Vehicle class: ", VehicleClass.values());
        LocalDate date = InputHelper.readDate("Test date");
        TestBooking b = bookings.book(s, type, vc, date);
        Menu.ok("Booked " + b.getId() + " on " + b.getTestDate());
    }

    private void removeBooking() {
        String id = InputHelper.readRequired("Booking ID to remove: ");
        bookings.require(id);
        if (InputHelper.readYesNo("Remove booking " + id + "?")) {
            bookings.remove(id);
            Menu.ok("Booking removed.");
        }
    }

    private void record() {
        TestBooking b = bookings.require(InputHelper.readRequired("Booking ID: "));
        int score = InputHelper.readInt("Score in % (0-100): ", 0, 100);
        String note = InputHelper.readLine("Examiner note (optional): ");
        TestResult r = results.record(b, score, note);
        Menu.ok("Result " + r.getId() + ": " + (r.isPassed() ? "PASSED" : "FAILED") + " with " + r.getScore() + "%.");
    }

    private void correct() {
        String id = InputHelper.readRequired("Result ID (e.g. G13-R001): ");
        results.require(id);
        int score = InputHelper.readInt("Corrected score in % (0-100): ", 0, 100);
        results.update(id, r -> r.setScore(score));
        Menu.ok("Score corrected.");
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "All results, highest score first", "Passed only", "Failed only",
                "Pass rate per test type");
        if (how == 0) {
            Menu.printList("Results by score", results.sorted(TestResultService.BY_SCORE_DESC));
        } else if (how == 1 || how == 2) {
            boolean wantPassed = how == 1;
            List<TestResult> list = results.filter(r -> r.isPassed() == wantPassed);
            list.sort(TestResultService.BY_SCORE_DESC);
            Menu.printList(wantPassed ? "Passed" : "Failed", list);
        } else {
            for (TestBooking.Type t : TestBooking.Type.values()) {
                List<TestResult> all = results.filter(r -> r.getBooking().getType() == t);
                long passed = all.stream().filter(TestResult::isPassed).count();
                String rate = all.isEmpty() ? "no results yet" : String.format("%d of %d passed (%.0f%%)", passed,
                        all.size(), 100.0 * passed / all.size());
                System.out.println("  " + t + ": " + rate);
            }
        }
    }

    /** Polymorphism: one loop over bookings and results through Reportable. */
    private void overview() {
        List<Reportable> mixed = new ArrayList<>(bookings.sorted(TestBookingService.BY_DATE));
        mixed.addAll(results.sorted(TestResultService.BY_SCORE_DESC));
        System.out.println("--- Test bookings and results (" + mixed.size() + ") ---");
        for (Reportable r : mixed) {
            System.out.println("  " + r.reportLine());
        }
    }
}
