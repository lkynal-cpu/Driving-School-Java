package ug.ac.vu.g13.students;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Manages learner's permit applications.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class PermitService extends BaseService<PermitApplication> {
    private final StudentService students;

    public PermitService(StudentService students) {
        super("permits.txt", "Permit application");
        this.students = students;
        load();
    }

    @Override
    protected PermitApplication fromFileLine(String line) {
        String[] f = Fields.split(line, 5);
        return new PermitApplication(f[0], students.require(f[1]), LocalDate.parse(f[2]),
                VehicleClass.valueOf(f[3]), PermitApplication.Status.valueOf(f[4]));
    }

    public PermitApplication apply(Student student, VehicleClass permitClass) {
        boolean open = anyMatch(p -> p.getStudent().equals(student) && p.getPermitClass() == permitClass
                && p.getStatus() != PermitApplication.Status.REJECTED);
        if (open) {
            throw new InvalidDataException(student.getFullName() + " already has a " + permitClass + " permit application.");
        }
        PermitApplication p = new PermitApplication(nextId('A'), student, LocalDate.now(), permitClass,
                PermitApplication.Status.PENDING);
        add(p);
        return p;
    }
}
