package ug.ac.vu.g13.lessons;

import java.time.LocalDate;
import java.time.LocalTime;

import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.instructors.Instructor;
import ug.ac.vu.g13.students.Student;

/**
 * A classroom lesson on road rules and signs.
 *
 * @author Member 5 (Lessons module owner)
 */
public class TheoryLesson extends Lesson {
    private final String topic;

    public TheoryLesson(String id, Student student, Instructor instructor, String topic, LocalDate date,
                        LocalTime start, int durationMinutes, Status status, String notes) {
        super(id, student, instructor, date, start, durationMinutes, status, notes);
        this.topic = Validate.text(topic, "Topic", 3, 60);
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String lessonType() {
        return "THEORY";
    }

    @Override
    protected String extraField() {
        return topic;
    }

    @Override
    protected String extraDescription() {
        return "Topic: " + topic;
    }
}
