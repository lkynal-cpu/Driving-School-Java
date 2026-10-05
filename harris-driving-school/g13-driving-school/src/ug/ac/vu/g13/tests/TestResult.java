package ug.ac.vu.g13.tests;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;

/**
 * The result of a test that was taken. The pass mark depends on the test type.
 *
 * @author Member 7 (Tests module owner)
 */
public class TestResult extends BaseRecord {
    public static final int THEORY_PASS_MARK = 60;
    public static final int PRACTICAL_PASS_MARK = 70;

    private final TestBooking booking;
    private int score;
    private final LocalDate resultDate;
    private String examinerNote;

    public TestResult(String id, TestBooking booking, int score, LocalDate resultDate, String examinerNote) {
        super(id);
        this.booking = Validate.notNull(booking, "Test booking");
        setScore(score);
        this.resultDate = Validate.date(resultDate, "Result date", LocalDate.of(2020, 1, 1), LocalDate.now().plusDays(1));
        setExaminerNote(examinerNote);
    }

    public TestBooking getBooking() {
        return booking;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Validate.intRange(score, "Score (%)", 0, 100);
    }

    public LocalDate getResultDate() {
        return resultDate;
    }

    public String getExaminerNote() {
        return examinerNote;
    }

    public void setExaminerNote(String note) {
        this.examinerNote = note == null ? "" : Validate.text(note, "Examiner note", 0, 100);
    }

    public int passMark() {
        return booking.getType() == TestBooking.Type.THEORY ? THEORY_PASS_MARK : PRACTICAL_PASS_MARK;
    }

    public boolean isPassed() {
        return score >= passMark();
    }

    @Override
    public String describe() {
        return String.format("%s | Test result | %-20s | %s %s | score %d%% (pass %d%%) | %s | %s", getId(),
                booking.getStudent().getFullName(), booking.getType(), booking.getVehicleClass(), score, passMark(),
                isPassed() ? "PASSED" : "FAILED", resultDate);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), booking.getId(), score, resultDate, examinerNote);
    }
}
