package ug.ac.vu.g13.lessons;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.courses.EnrollmentService;
import ug.ac.vu.g13.instructors.Instructor;
import ug.ac.vu.g13.instructors.InstructorService;
import ug.ac.vu.g13.instructors.InstructorUnavailableException;
import ug.ac.vu.g13.instructors.ShiftService;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.students.StudentService;
import ug.ac.vu.g13.vehicles.Vehicle;
import ug.ac.vu.g13.vehicles.VehicleNotAvailableException;
import ug.ac.vu.g13.vehicles.VehicleService;

/**
 * Schedules and manages lessons. This is where the Students, Instructors,
 * Vehicles and Courses modules meet.
 *
 * @author Member 5 (Lessons module owner)
 */
public class LessonService extends BaseService<Lesson> {
    public static final Comparator<Lesson> BY_DATE_TIME = Comparator.comparing(Lesson::getDate)
            .thenComparing(Lesson::getStart).thenComparing(Lesson::getId);

    private final StudentService students;
    private final InstructorService instructors;
    private final VehicleService vehicles;
    private final ShiftService shifts;
    private final EnrollmentService enrollments;

    public LessonService(StudentService students, InstructorService instructors, VehicleService vehicles,
                         ShiftService shifts, EnrollmentService enrollments) {
        super("lessons.txt", "Lesson");
        this.students = students;
        this.instructors = instructors;
        this.vehicles = vehicles;
        this.shifts = shifts;
        this.enrollments = enrollments;
        load();
    }

    @Override
    protected Lesson fromFileLine(String line) {
        String[] f = Fields.split(line, 10);
        Student s = students.require(f[2]);
        Instructor i = instructors.require(f[3]);
        LocalDate date = LocalDate.parse(f[4]);
        LocalTime start = LocalTime.parse(f[5]);
        int minutes = Integer.parseInt(f[6]);
        Lesson.Status st = Lesson.Status.valueOf(f[7]);
        if (f[0].equals("PRACTICAL")) {
            return new PracticalLesson(f[1], s, i, vehicles.require(f[9]), date, start, minutes, st, f[8]);
        }
        return new TheoryLesson(f[1], s, i, f[9], date, start, minutes, st, f[8]);
    }

    public PracticalLesson schedulePractical(Student student, Instructor instructor, Vehicle vehicle, LocalDate date,
                                             LocalTime start, int minutes, String notes)
            throws SchedulingConflictException, InstructorUnavailableException, VehicleNotAvailableException {
        if (!enrollments.hasActiveFor(student, vehicle.getVehicleClass())) {
            throw new InvalidDataException(student.getFullName() + " has no active " + vehicle.getVehicleClass()
                    + " course, so cannot take a practical lesson on a " + vehicle.typeLabel() + ".");
        }
        if (!instructor.isQualifiedFor(vehicle.getVehicleClass())) {
            throw new InvalidDataException(instructor.getFullName() + " is not qualified to teach "
                    + vehicle.getVehicleClass() + ".");
        }
        PracticalLesson lesson = new PracticalLesson(nextId('L'), student, instructor, vehicle, date, start, minutes,
                Lesson.Status.SCHEDULED, notes);
        checkDate(date);
        shifts.ensureWorking(instructor, date, start, minutes);
        vehicles.ensureUsable(vehicle);
        checkFree(lesson);
        add(lesson);
        return lesson;
    }

    public TheoryLesson scheduleTheory(Student student, Instructor instructor, String topic, LocalDate date,
                                       LocalTime start, int minutes, String notes)
            throws SchedulingConflictException, InstructorUnavailableException {
        if (!enrollments.hasActive(student)) {
            throw new InvalidDataException(student.getFullName() + " has no active course.");
        }
        TheoryLesson lesson = new TheoryLesson(nextId('L'), student, instructor, topic, date, start, minutes,
                Lesson.Status.SCHEDULED, notes);
        checkDate(date);
        shifts.ensureWorking(instructor, date, start, minutes);
        checkFree(lesson);
        add(lesson);
        return lesson;
    }

    private void checkDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDataException("A new lesson cannot be scheduled in the past.");
        }
    }

    private void checkFree(Lesson n) throws SchedulingConflictException {
        for (Lesson other : getAll()) {
            if (other.getStatus() != Lesson.Status.SCHEDULED
                    || !other.overlapsWith(n.getDate(), n.getStart(), n.getDurationMinutes())) {
                continue;
            }
            if (other.getInstructor().equals(n.getInstructor())) {
                throw new SchedulingConflictException("Instructor " + n.getInstructor().getFullName(), other);
            }
            if (other.getStudent().equals(n.getStudent())) {
                throw new SchedulingConflictException("Student " + n.getStudent().getFullName(), other);
            }
            if (n instanceof PracticalLesson && other instanceof PracticalLesson
                    && ((PracticalLesson) other).getVehicle().equals(((PracticalLesson) n).getVehicle())) {
                throw new SchedulingConflictException("Vehicle " + ((PracticalLesson) n).getVehicle().getPlate(), other);
            }
        }
    }

    public void complete(String id) {
        changeStatus(id, Lesson.Status.COMPLETED);
    }

    public void cancel(String id) {
        changeStatus(id, Lesson.Status.CANCELLED);
    }

    private void changeStatus(String id, Lesson.Status newStatus) {
        Lesson l = require(id);
        if (l.getStatus() != Lesson.Status.SCHEDULED) {
            throw new InvalidDataException("Lesson " + l.getId() + " is already " + l.getStatus() + ".");
        }
        update(id, x -> x.setStatus(newStatus));
    }

    public int completedHours(Instructor instructor) {
        int minutes = 0;
        for (Lesson l : getAll()) {
            if (l.getInstructor().equals(instructor) && l.getStatus() == Lesson.Status.COMPLETED) {
                minutes += l.getDurationMinutes();
            }
        }
        return minutes;
    }
}
