package ug.ac.vu.g13.tests;

import java.time.LocalDate;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;
import ug.ac.vu.g13.courses.EnrollmentService;
import ug.ac.vu.g13.fees.InvoiceService;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.students.StudentService;

/**
 * Manages test bookings and checks the eligibility rules:
 * an active course for the class, at least half the fees paid, a passed theory
 * test before a practical test, and no repeat of a test already passed.
 *
 * @author Member 7 (Tests module owner)
 */
public class TestBookingService extends BaseService<TestBooking> {
    public static final double MIN_FEES_PAID = 0.5;
    public static final Comparator<TestBooking> BY_DATE = Comparator.comparing(TestBooking::getTestDate)
            .thenComparing(TestBooking::getId);

    private final StudentService students;
    private final EnrollmentService enrollments;
    private final InvoiceService invoices;
    private TestResultService results;

    public TestBookingService(StudentService students, EnrollmentService enrollments, InvoiceService invoices) {
        super("test_bookings.txt", "Test booking");
        this.students = students;
        this.enrollments = enrollments;
        this.invoices = invoices;
        load();
    }

    /** Called once by the application so the two test services can see each other. */
    public void linkResults(TestResultService results) {
        this.results = results;
    }

    @Override
    protected TestBooking fromFileLine(String line) {
        String[] f = Fields.split(line, 6);
        return new TestBooking(f[0], students.require(f[1]), TestBooking.Type.valueOf(f[2]),
                VehicleClass.valueOf(f[3]), LocalDate.parse(f[4]), TestBooking.Status.valueOf(f[5]));
    }

    public TestBooking book(Student student, TestBooking.Type type, VehicleClass vc, LocalDate date)
            throws NotEligibleForTestException {
        if (!enrollments.hasActiveFor(student, vc)) {
            throw new NotEligibleForTestException(student.getFullName() + " has no active " + vc + " course.");
        }
        if (!invoices.hasPaidAtLeast(student, MIN_FEES_PAID)) {
            throw new NotEligibleForTestException("less than " + (int) (MIN_FEES_PAID * 100) + "% of the fees are paid.");
        }
        if (results != null && results.hasPassed(student, type, vc)) {
            throw new NotEligibleForTestException("the " + type + " " + vc + " test is already passed.");
        }
        if (type == TestBooking.Type.PRACTICAL && (results == null || !results.hasPassed(student, TestBooking.Type.THEORY, vc))) {
            throw new NotEligibleForTestException("the theory test must be passed before a practical test.");
        }
        if (anyMatch(b -> b.getStudent().equals(student) && b.getType() == type && b.getVehicleClass() == vc
                && b.getStatus() == TestBooking.Status.BOOKED)) {
            throw new NotEligibleForTestException("a " + type + " " + vc + " test is already booked.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDataException("A test cannot be booked in the past.");
        }
        TestBooking b = new TestBooking(nextId('T'), student, type, vc, date, TestBooking.Status.BOOKED);
        add(b);
        return b;
    }

    public void cancel(String id) {
        TestBooking b = require(id);
        if (b.getStatus() != TestBooking.Status.BOOKED) {
            throw new InvalidDataException("Only a BOOKED test can be cancelled (this one is " + b.getStatus() + ").");
        }
        update(id, x -> x.setStatus(TestBooking.Status.CANCELLED));
    }
}
