package ug.ac.vu.g13.tests;

import java.time.LocalDate;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;
import ug.ac.vu.g13.students.Student;

/**
 * Manages test results.
 *
 * @author Member 7 (Tests module owner)
 */
public class TestResultService extends BaseService<TestResult> {
    public static final Comparator<TestResult> BY_SCORE_DESC = Comparator.comparingInt(TestResult::getScore).reversed()
            .thenComparing(TestResult::getId);

    private final TestBookingService bookings;

    public TestResultService(TestBookingService bookings) {
        super("test_results.txt", "Test result");
        this.bookings = bookings;
        load();
        bookings.addRemovalGuard(b -> anyMatch(r -> r.getBooking().equals(b)) ? "it already has a result" : null);
    }

    @Override
    protected TestResult fromFileLine(String line) {
        String[] f = Fields.split(line, 5);
        return new TestResult(f[0], bookings.require(f[1]), Integer.parseInt(f[2]), LocalDate.parse(f[3]), f[4]);
    }

    public TestResult record(TestBooking booking, int score, String note) {
        if (booking.getStatus() != TestBooking.Status.BOOKED) {
            throw new InvalidDataException("Booking " + booking.getId() + " is " + booking.getStatus()
                    + ", so a result cannot be recorded.");
        }
        TestResult r = new TestResult(nextId('R'), booking, score, LocalDate.now(), note);
        add(r);
        bookings.update(booking.getId(), b -> b.setStatus(TestBooking.Status.TAKEN));
        return r;
    }

    public boolean hasPassed(Student student, TestBooking.Type type, VehicleClass vc) {
        return anyMatch(r -> r.isPassed() && r.getBooking().getStudent().equals(student)
                && r.getBooking().getType() == type && r.getBooking().getVehicleClass() == vc);
    }
}
