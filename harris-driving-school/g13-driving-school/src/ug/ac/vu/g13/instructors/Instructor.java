package ug.ac.vu.g13.instructors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A driving instructor and the vehicle classes he or she may teach.
 *
 * @author Member 2 (Instructors module owner)
 */
public class Instructor extends BaseRecord {
    private String fullName;
    private String phone;
    private final EnumSet<VehicleClass> qualifications = EnumSet.noneOf(VehicleClass.class);
    private int yearsExperience;
    private boolean active;

    public Instructor(String id, String fullName, String phone, Set<VehicleClass> qualifications,
                      int yearsExperience, boolean active) {
        super(id);
        setFullName(fullName);
        setPhone(phone);
        setQualifications(qualifications);
        setYearsExperience(yearsExperience);
        this.active = active;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Validate.name(fullName, "Instructor name");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Validate.phone(phone);
    }

    public Set<VehicleClass> getQualifications() {
        return EnumSet.copyOf(qualifications);
    }

    public void setQualifications(Set<VehicleClass> newOnes) {
        if (newOnes == null || newOnes.isEmpty()) {
            throw new InvalidDataException("An instructor must be qualified for at least one vehicle class.");
        }
        qualifications.clear();
        qualifications.addAll(newOnes);
    }

    public boolean isQualifiedFor(VehicleClass vc) {
        return qualifications.contains(vc);
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(int yearsExperience) {
        this.yearsExperience = Validate.intRange(yearsExperience, "Years of experience", 0, 50);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String describe() {
        return String.format("%s | %-22s | %s | teaches %s | %d yrs | %s",
                getId(), fullName, phone, qualifications, yearsExperience, active ? "ACTIVE" : "INACTIVE");
    }

    @Override
    public String toFileLine() {
        List<String> names = new ArrayList<>();
        for (VehicleClass vc : qualifications) {
            names.add(vc.name());
        }
        return Fields.join(getId(), fullName, phone, String.join(",", names), yearsExperience, active);
    }
}
