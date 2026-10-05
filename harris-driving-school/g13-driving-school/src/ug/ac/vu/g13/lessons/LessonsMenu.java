package ug.ac.vu.g13.lessons;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.instructors.Instructor;
import ug.ac.vu.g13.instructors.InstructorService;
import ug.ac.vu.g13.students.Student;
import ug.ac.vu.g13.students.StudentService;
import ug.ac.vu.g13.vehicles.Vehicle;
import ug.ac.vu.g13.vehicles.VehicleService;

/**
 * Submenu for the Lessons module.
 *
 * @author Member 5 (Lessons module owner)
 */
public class LessonsMenu extends AbstractModuleMenu {
    private final LessonService lessons;
    private final StudentService students;
    private final InstructorService instructors;
    private final VehicleService vehicles;

    public LessonsMenu(LessonService lessons, StudentService students, InstructorService instructors,
                       VehicleService vehicles) {
        this.lessons = lessons;
        this.students = students;
        this.instructors = instructors;
        this.vehicles = vehicles;
    }

    @Override
    public String getName() {
        return "Lessons";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Schedule a practical lesson", "Schedule a theory lesson", "List all lessons",
            "Mark a lesson completed", "Cancel a lesson", "Edit lesson notes", "Remove a lesson",
            "Report: lessons (sorted / filtered)", "Overview of all lessons by type"
        };
    }

    @Override
    protected void handle(int choice) throws Exception {
        switch (choice) {
            case 1: practical(); break;
            case 2: theory(); break;
            case 3: Menu.printList("All lessons", lessons.sorted(LessonService.BY_DATE_TIME)); break;
            case 4: lessons.complete(InputHelper.readRequired("Lesson ID (e.g. G13-L001): ")); Menu.ok("Lesson completed."); break;
            case 5: lessons.cancel(InputHelper.readRequired("Lesson ID: ")); Menu.ok("Lesson cancelled."); break;
            case 6: notes(); break;
            case 7: remove(); break;
            case 8: report(); break;
            case 9: overview(); break;
            default: break;
        }
    }

    private void practical() throws Exception {
        Student s = students.require(InputHelper.readRequired("Student ID: "));
        Instructor i = instructors.require(InputHelper.readRequired("Instructor ID: "));
        String vt = InputHelper.readRequired("Vehicle ID or plate: ");
        Vehicle v = vehicles.findByIdOrPlate(vt);
        if (v == null) {
            throw new ug.ac.vu.g13.core.RecordNotFoundException("Vehicle", vt);
        }
        LocalDate date = InputHelper.readDate("Lesson date");
        LocalTime start = InputHelper.readTime("Start time");
        int minutes = InputHelper.readInt("Duration in minutes (30-240): ", 30, 240);
        String notes = InputHelper.readLine("Notes (optional): ");
        PracticalLesson l = lessons.schedulePractical(s, i, v, date, start, minutes, notes);
        Menu.ok("Scheduled " + l.getId() + ": " + l.getDate() + " " + l.getStart() + "-" + l.getEnd());
    }

    private void theory() throws Exception {
        Student s = students.require(InputHelper.readRequired("Student ID: "));
        Instructor i = instructors.require(InputHelper.readRequired("Instructor ID: "));
        String topic = InputHelper.readRequired("Topic (e.g. Road signs): ");
        LocalDate date = InputHelper.readDate("Lesson date");
        LocalTime start = InputHelper.readTime("Start time");
        int minutes = InputHelper.readInt("Duration in minutes (30-240): ", 30, 240);
        String notes = InputHelper.readLine("Notes (optional): ");
        TheoryLesson l = lessons.scheduleTheory(s, i, topic, date, start, minutes, notes);
        Menu.ok("Scheduled " + l.getId() + ": " + l.getDate() + " " + l.getStart() + "-" + l.getEnd());
    }

    private void notes() {
        String id = InputHelper.readRequired("Lesson ID: ");
        lessons.require(id);
        String text = InputHelper.readLine("New notes (max 100 characters): ");
        lessons.update(id, l -> l.setNotes(text));
        Menu.ok("Notes updated.");
    }

    private void remove() {
        String id = InputHelper.readRequired("Lesson ID to remove: ");
        lessons.require(id);
        if (InputHelper.readYesNo("Remove lesson " + id + "?")) {
            lessons.remove(id);
            Menu.ok("Lesson removed.");
        }
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "Upcoming scheduled lessons", "Lessons of one student",
                "Lessons of one instructor", "Lessons with one status", "Hours taught per instructor");
        if (how == 0) {
            List<Lesson> list = lessons.filter(l -> l.getStatus() == Lesson.Status.SCHEDULED
                    && !l.getDate().isBefore(LocalDate.now()));
            list.sort(LessonService.BY_DATE_TIME);
            Menu.printList("Upcoming lessons", list);
        } else if (how == 1) {
            Student s = students.require(InputHelper.readRequired("Student ID: "));
            Menu.printList("Lessons of " + s.getFullName(), lessons.filter(l -> l.getStudent().equals(s)).stream()
                    .sorted(LessonService.BY_DATE_TIME).collect(java.util.stream.Collectors.toList()));
        } else if (how == 2) {
            Instructor i = instructors.require(InputHelper.readRequired("Instructor ID: "));
            Menu.printList("Lessons of " + i.getFullName(), lessons.filter(l -> l.getInstructor().equals(i)).stream()
                    .sorted(LessonService.BY_DATE_TIME).collect(java.util.stream.Collectors.toList()));
        } else if (how == 3) {
            Lesson.Status st = InputHelper.readEnum("Status: ", Lesson.Status.values());
            List<Lesson> list = lessons.filter(l -> l.getStatus() == st);
            list.sort(LessonService.BY_DATE_TIME);
            Menu.printList("Lessons: " + st, list);
        } else {
            List<Instructor> list = new ArrayList<>(instructors.getAll());
            list.sort((a, b) -> Integer.compare(lessons.completedHours(b), lessons.completedHours(a)));
            System.out.println("--- Completed teaching time per instructor ---");
            for (Instructor i : list) {
                int m = lessons.completedHours(i);
                System.out.println("  " + i.getFullName() + ": " + (m / 60) + " h " + (m % 60) + " min");
            }
        }
    }

    /** Polymorphism: one loop over PracticalLesson and TheoryLesson objects through the Lesson type. */
    private void overview() {
        int practical = 0;
        int theory = 0;
        System.out.println("--- Lessons overview (" + lessons.size() + ") ---");
        for (Lesson l : lessons.sorted(LessonService.BY_DATE_TIME)) {
            System.out.println("  [" + l.lessonType() + "] " + l.reportLine());
            if (l instanceof PracticalLesson) {
                practical++;
            } else {
                theory++;
            }
        }
        System.out.println("  Practical: " + practical + ", Theory: " + theory);
    }
}
