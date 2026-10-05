package ug.ac.vu.g13.students;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;

/**
 * Manages the list of students.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class StudentService extends BaseService<Student> {
    public static final Comparator<Student> BY_NAME = Comparator.comparing(s -> s.getFullName().toLowerCase());
    public static final Comparator<Student> BY_REGISTRATION = Comparator.comparing(Student::getRegisteredOn).thenComparing(Student::getId);
    public static final Comparator<Student> BY_AGE = Comparator.comparingInt(Student::getAge);

    public StudentService() {
        super("students.txt", "Student");
        load();
    }

    @Override
    protected Student fromFileLine(String line) {
        String[] f = Fields.split(line, 6);
        return new Student(f[0], f[1], f[2], LocalDate.parse(f[3]), f[4], LocalDate.parse(f[5]));
    }

    public Student register(String name, String phone, LocalDate dob, String address) throws DuplicateStudentException {
        Student s = new Student(nextId('S'), name, phone, dob, address, LocalDate.now());
        for (Student existing : getAll()) {
            if (existing.getFullName().equalsIgnoreCase(s.getFullName()) && existing.getDateOfBirth().equals(s.getDateOfBirth())) {
                throw new DuplicateStudentException(existing);
            }
        }
        add(s);
        return s;
    }

    public List<Student> searchByName(String part) {
        String p = part.toLowerCase();
        return filter(s -> s.getFullName().toLowerCase().contains(p));
    }
}
