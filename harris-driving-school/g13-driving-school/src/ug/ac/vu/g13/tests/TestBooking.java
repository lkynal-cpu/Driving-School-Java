package ug.ac.vu.g13.tests;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;
import ug.ac.vu.g13.students.Student;

/**
 * A student's booking for a theory or practical driving test.
 *
 * @author Member 7 (Tests module owner)
 */
public class TestBooking extends BaseRecord {
    public enum Type { THEORY, PRACTICAL }

    public enum Status { BOOKED, TAKEN, CANCELLED }

    private final Student student;
    private final Type type;
    private final VehicleClass vehicleClass;
    private LocalDate testDate;
    private Status status;

    public TestBooking(String id, Student student, Type type, VehicleClass vehicleClass, LocalDate testDate, Status status) {
        super(id);
        this.student = Validate.notNull(student, "Student");
        this.type = Validate.notNull(type, "Test type");
        this.vehicleClass = Validate.notNull(vehicleClass, "Vehicle class");
        setTestDate(testDate);
        setStatus(status);
    }

    public Student getStudent() {
        return student;
    }

    public Type getType() {
        return type;
    }

    public VehicleClass getVehicleClass() {
        return vehicleClass;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = Validate.date(testDate, "Test date", LocalDate.of(2020, 1, 1), LocalDate.now().plusYears(1));
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Validate.notNull(status, "Status");
    }

    @Override
    public String describe() {
        return String.format("%s | Test booking | %-20s | %s %s | %s | %s", getId(), student.getFullName(), type,
                vehicleClass, testDate, status);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), student.getId(), type.name(), vehicleClass.name(), testDate, status.name());
    }
}
