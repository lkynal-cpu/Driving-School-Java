package ug.ac.vu.g13.instructors;

import java.time.DayOfWeek;
import java.time.LocalTime;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.Validate;

/**
 * A weekly working period of an instructor. Linked to an Instructor by object reference.
 *
 * @author Member 2 (Instructors module owner)
 */
public class Shift extends BaseRecord {
    private final Instructor instructor;
    private final DayOfWeek day;
    private final LocalTime start;
    private final LocalTime end;

    public Shift(String id, Instructor instructor, DayOfWeek day, LocalTime start, LocalTime end) {
        super(id);
        this.instructor = Validate.notNull(instructor, "Instructor");
        this.day = Validate.notNull(day, "Day");
        Validate.notNull(start, "Start time");
        Validate.notNull(end, "End time");
        if (!end.isAfter(start)) {
            throw new InvalidDataException("A shift must end after it starts.");
        }
        if (start.isBefore(LocalTime.of(5, 0)) || end.isAfter(LocalTime.of(22, 0))) {
            throw new InvalidDataException("Shifts must be between 05:00 and 22:00.");
        }
        this.start = start;
        this.end = end;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public boolean overlaps(Shift other) {
        return instructor.equals(other.instructor) && day == other.day
                && start.isBefore(other.end) && other.start.isBefore(end);
    }

    @Override
    public String describe() {
        return String.format("%s | Shift | %s (%s) | %s %s-%s",
                getId(), instructor.getFullName(), instructor.getId(), day, start, end);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), instructor.getId(), day.name(), start, end);
    }
}
