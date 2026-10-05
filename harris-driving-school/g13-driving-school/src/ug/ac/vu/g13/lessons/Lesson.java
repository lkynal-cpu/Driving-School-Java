package ug.ac.vu.g13.lessons;

import java.time.LocalDate;
import java.time.LocalTime;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.instructors.Instructor;
import ug.ac.vu.g13.students.Student;

/**
 * Abstract parent of every lesson. PracticalLesson and TheoryLesson extend it.
 * Links the Students and Instructors modules by object reference.
 *
 * @author Member 5 (Lessons module owner)
 */
public abstract class Lesson extends BaseRecord {
    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private final Student student;
    private final Instructor instructor;
    private final LocalDate date;
    private final LocalTime start;
    private final int durationMinutes;
    private Status status;
    private String notes;

    protected Lesson(String id, Student student, Instructor instructor, LocalDate date, LocalTime start,
                     int durationMinutes, Status status, String notes) {
        super(id);
        this.student = Validate.notNull(student, "Student");
        this.instructor = Validate.notNull(instructor, "Instructor");
        this.date = Validate.date(date, "Lesson date", LocalDate.of(2020, 1, 1), LocalDate.now().plusYears(2));
        Validate.notNull(start, "Start time");
        this.durationMinutes = Validate.intRange(durationMinutes, "Duration (minutes)", 30, 240);
        if (start.isBefore(LocalTime.of(6, 0)) || start.plusMinutes(durationMinutes).isAfter(LocalTime.of(21, 0))
                || start.plusMinutes(durationMinutes).isBefore(start)) {
            throw new InvalidDataException("Lessons must start after 06:00 and finish by 21:00.");
        }
        this.start = start;
        setStatus(status);
        setNotes(notes);
    }

    public abstract String lessonType();

    /** The extra detail saved to file (vehicle ID or topic). */
    protected abstract String extraField();

    protected abstract String extraDescription();

    public Student getStudent() {
        return student;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return start.plusMinutes(durationMinutes);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Validate.notNull(status, "Status");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : Validate.text(notes.isEmpty() ? " " : notes, "Notes", 0, 100);
    }

    public boolean overlapsWith(LocalDate otherDate, LocalTime otherStart, int otherMinutes) {
        return date.equals(otherDate) && start.isBefore(otherStart.plusMinutes(otherMinutes))
                && otherStart.isBefore(getEnd());
    }

    @Override
    public String describe() {
        return String.format("%s | %-9s | %s %s-%s | %s -> %s | %s | %s",
                getId(), lessonType(), date, start, getEnd(), student.getFullName(), instructor.getFullName(),
                extraDescription(), status);
    }

    @Override
    public String toFileLine() {
        return Fields.join(lessonType(), getId(), student.getId(), instructor.getId(), date, start,
                durationMinutes, status.name(), notes, extraField());
    }
}
