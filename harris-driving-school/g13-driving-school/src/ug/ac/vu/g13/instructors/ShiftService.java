package ug.ac.vu.g13.instructors;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;

/**
 * Manages instructor working shifts and answers "is this instructor free to teach then?".
 *
 * @author Member 2 (Instructors module owner)
 */
public class ShiftService extends BaseService<Shift> {
    private final InstructorService instructors;

    public ShiftService(InstructorService instructors) {
        super("shifts.txt", "Shift");
        this.instructors = instructors;
        load();
    }

    @Override
    protected Shift fromFileLine(String line) {
        String[] f = Fields.split(line, 5);
        return new Shift(f[0], instructors.require(f[1]), DayOfWeek.valueOf(f[2]),
                LocalTime.parse(f[3]), LocalTime.parse(f[4]));
    }

    public Shift addShift(Instructor instructor, DayOfWeek day, LocalTime start, LocalTime end) {
        Shift s = new Shift(nextId('H'), instructor, day, start, end);
        if (anyMatch(existing -> existing.overlaps(s))) {
            throw new InvalidDataException("This shift overlaps another shift of the same instructor.");
        }
        add(s);
        return s;
    }

    /**
     * Instructors with no shifts recorded are treated as flexible. Instructors who
     * have shifts must teach inside one of them.
     */
    public void ensureWorking(Instructor instructor, LocalDate date, LocalTime start, int minutes)
            throws InstructorUnavailableException {
        if (!instructor.isActive()) {
            throw new InstructorUnavailableException(instructor.getFullName() + " is not active.");
        }
        if (!anyMatch(s -> s.getInstructor().equals(instructor))) {
            return;
        }
        LocalTime end = start.plusMinutes(minutes);
        boolean inside = anyMatch(s -> s.getInstructor().equals(instructor) && s.getDay() == date.getDayOfWeek()
                && !start.isBefore(s.getStart()) && !end.isAfter(s.getEnd()) && end.isAfter(start));
        if (!inside) {
            throw new InstructorUnavailableException(instructor.getFullName() + " is not on shift on "
                    + date.getDayOfWeek() + " at " + start + "-" + end + ".");
        }
    }
}
