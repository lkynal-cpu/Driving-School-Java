package ug.ac.vu.g13.students;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A student's application for a learner's permit. Linked to a Student by
 * object reference.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class PermitApplication extends BaseRecord {
    public enum Status { PENDING, APPROVED, REJECTED }

    private final Student student;
    private final LocalDate appliedOn;
    private final VehicleClass permitClass;
    private Status status;

    public PermitApplication(String id, Student student, LocalDate appliedOn, VehicleClass permitClass, Status status) {
        super(id);
        this.student = Validate.notNull(student, "Student");
        this.appliedOn = Validate.date(appliedOn, "Application date", LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
        this.permitClass = Validate.notNull(permitClass, "Permit class");
        setStatus(status);
    }

    public Student getStudent() {
        return student;
    }

    public LocalDate getAppliedOn() {
        return appliedOn;
    }

    public VehicleClass getPermitClass() {
        return permitClass;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Validate.notNull(status, "Status");
    }

    @Override
    public String describe() {
        return String.format("%s | Permit application | %s (%s) | %s | %s | %s",
                getId(), student.getFullName(), student.getId(), permitClass, appliedOn, status);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), student.getId(), appliedOn, permitClass.name(), status.name());
    }
}
