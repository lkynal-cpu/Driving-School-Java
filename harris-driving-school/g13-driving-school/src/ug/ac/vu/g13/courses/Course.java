package ug.ac.vu.g13.courses;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A course the school sells, for one vehicle class.
 *
 * @author Member 4 (Courses module owner)
 */
public class Course extends BaseRecord {
    private String name;
    private final VehicleClass vehicleClass;
    private int durationWeeks;
    private int practicalHours;
    private double fee;
    private int capacity;

    public Course(String id, String name, VehicleClass vehicleClass, int durationWeeks, int practicalHours,
                  double fee, int capacity) {
        super(id);
        setName(name);
        this.vehicleClass = Validate.notNull(vehicleClass, "Vehicle class");
        setDurationWeeks(durationWeeks);
        setPracticalHours(practicalHours);
        setFee(fee);
        setCapacity(capacity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validate.text(name, "Course name", 3, 50);
    }

    public VehicleClass getVehicleClass() {
        return vehicleClass;
    }

    public int getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(int durationWeeks) {
        this.durationWeeks = Validate.intRange(durationWeeks, "Duration (weeks)", 1, 52);
    }

    public int getPracticalHours() {
        return practicalHours;
    }

    public void setPracticalHours(int practicalHours) {
        this.practicalHours = Validate.intRange(practicalHours, "Practical hours", 1, 200);
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = Validate.doubleRange(fee, "Course fee (UGX)", 1000, 10_000_000);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Validate.intRange(capacity, "Capacity", 1, 60);
    }

    @Override
    public String describe() {
        return String.format("%s | %-24s | %-10s | %2d wks | %3d practical hrs | %s | max %d learners",
                getId(), name, vehicleClass, durationWeeks, practicalHours, Validate.money(fee), capacity);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), name, vehicleClass.name(), durationWeeks, practicalHours, fee, capacity);
    }
}
