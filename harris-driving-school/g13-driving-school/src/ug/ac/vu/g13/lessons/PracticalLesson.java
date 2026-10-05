package ug.ac.vu.g13.lessons;

import java.time.LocalDate;
import java.time.LocalTime;

import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.instructors.Instructor;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.vehicles.Vehicle;

/**
 * A behind-the-wheel lesson. Also links to a Vehicle by object reference.
 *
 * @author Member 5 (Lessons module owner)
 */
public class PracticalLesson extends Lesson {
    private final Vehicle vehicle;

    public PracticalLesson(String id, Student student, Instructor instructor, Vehicle vehicle, LocalDate date,
                           LocalTime start, int durationMinutes, Status status, String notes) {
        super(id, student, instructor, date, start, durationMinutes, status, notes);
        this.vehicle = Validate.notNull(vehicle, "Vehicle");
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public String lessonType() {
        return "PRACTICAL";
    }

    @Override
    protected String extraField() {
        return vehicle.getId();
    }

    @Override
    protected String extraDescription() {
        return vehicle.typeLabel() + " " + vehicle.getPlate();
    }
}
