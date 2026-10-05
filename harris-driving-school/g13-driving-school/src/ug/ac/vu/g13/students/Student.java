package ug.ac.vu.g13.students;

import java.time.LocalDate;
import java.time.Period;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;

/**
 * A learner driver registered at the school.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class Student extends BaseRecord {
    public static final int MIN_AGE = 16;
    public static final int MAX_AGE = 90;

    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private final LocalDate registeredOn;

    public Student(String id, String fullName, String phone, LocalDate dateOfBirth, String address, LocalDate registeredOn) {
        super(id);
        setFullName(fullName);
        setPhone(phone);
        setDateOfBirth(dateOfBirth);
        setAddress(address);
        this.registeredOn = Validate.date(registeredOn, "Registration date", LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Validate.name(fullName, "Full name");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Validate.phone(phone);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        Validate.notNull(dateOfBirth, "Date of birth");
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (dateOfBirth.isAfter(LocalDate.now()) || age < MIN_AGE || age > MAX_AGE) {
            throw new ug.ac.vu.g13.core.InvalidDataException(
                    "A student must be between " + MIN_AGE + " and " + MAX_AGE + " years old.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = Validate.text(address, "Address", 2, 80);
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String describe() {
        return String.format("%s | %-22s | age %2d | %s | %s | joined %s",
                getId(), fullName, getAge(), phone, address, registeredOn);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), fullName, phone, dateOfBirth, address, registeredOn);
    }
}
