package ug.ac.vu.g13.students;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.Reportable;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Submenu for the Students module.
 *
 * @author Kefas (Students module owner, Integration lead)
 */
public class StudentsMenu extends AbstractModuleMenu {
    private final StudentService students;
    private final PermitService permits;

    public StudentsMenu(StudentService students, PermitService permits) {
        this.students = students;
        this.permits = permits;
    }

    @Override
    public String getName() {
        return "Students";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Register a new student", "List all students", "Search students by name",
            "Update a student", "Remove a student", "Report: students sorted",
            "Add a permit application", "Change permit application status",
            "List permit applications", "Overview of students and permits"
        };
    }

    @Override
    protected void handle(int choice) throws Exception {
        switch (choice) {
            case 1: register(); break;
            case 2: Menu.printList("All students", students.sorted(StudentService.BY_NAME)); break;
            case 3: search(); break;
            case 4: update(); break;
            case 5: remove(); break;
            case 6: report(); break;
            case 7: addPermit(); break;
            case 8: changePermit(); break;
            case 9: Menu.printList("Permit applications", permits.getAll()); break;
            case 10: overview(); break;
            default: break;
        }
    }

    private void register() throws DuplicateStudentException {
        String name = InputHelper.readRequired("Full name: ");
        String phone = InputHelper.readRequired("Phone (e.g. 0700000000, use an invented number): ");
        LocalDate dob = InputHelper.readDate("Date of birth");
        String address = InputHelper.readRequired("Address / village / town: ");
        Student s = students.register(name, phone, dob, address);
        Menu.ok("Registered " + s.getFullName() + " with ID " + s.getId());
    }

    private void search() {
        String part = InputHelper.readRequired("Part of the name: ");
        Menu.printList("Matches", students.searchByName(part));
    }

    private void update() {
        String id = InputHelper.readRequired("Student ID (e.g. G13-S001): ");
        students.require(id);
        int field = InputHelper.readChoice("What do you want to change? ", "Full name", "Phone", "Address", "Date of birth");
        switch (field) {
            case 0: {
                String v = InputHelper.readRequired("New full name: ");
                students.update(id, s -> s.setFullName(v));
                break;
            }
            case 1: {
                String v = InputHelper.readRequired("New phone: ");
                students.update(id, s -> s.setPhone(v));
                break;
            }
            case 2: {
                String v = InputHelper.readRequired("New address: ");
                students.update(id, s -> s.setAddress(v));
                break;
            }
            default: {
                LocalDate v = InputHelper.readDate("New date of birth");
                students.update(id, s -> s.setDateOfBirth(v));
                break;
            }
        }
        Menu.ok("Student updated.");
    }

    private void remove() {
        String id = InputHelper.readRequired("Student ID to remove: ");
        Student s = students.require(id);
        if (InputHelper.readYesNo("Remove " + s.getFullName() + "?")) {
            students.remove(id);
            Menu.ok("Student removed.");
        }
    }

    private void report() {
        int how = InputHelper.readChoice("Sort by: ", "Name (A-Z)", "Registration date (oldest first)", "Age (youngest first)");
        if (how == 0) {
            Menu.printList("Students by name", students.sorted(StudentService.BY_NAME));
        } else if (how == 1) {
            Menu.printList("Students by registration date", students.sorted(StudentService.BY_REGISTRATION));
        } else {
            Menu.printList("Students by age", students.sorted(StudentService.BY_AGE));
        }
    }

    private void addPermit() {
        Student s = students.require(InputHelper.readRequired("Student ID: "));
        VehicleClass vc = InputHelper.readEnum("Permit for which vehicle class? ", VehicleClass.values());
        PermitApplication p = permits.apply(s, vc);
        Menu.ok("Application " + p.getId() + " recorded as PENDING.");
    }

    private void changePermit() {
        String id = InputHelper.readRequired("Permit application ID (e.g. G13-A001): ");
        permits.require(id);
        PermitApplication.Status st = InputHelper.readEnum("New status: ", PermitApplication.Status.values());
        permits.update(id, p -> p.setStatus(st));
        Menu.ok("Status updated.");
    }

    /** Polymorphism: one loop over two different kinds of record through Reportable. */
    private void overview() {
        List<Reportable> mixed = new ArrayList<>(students.sorted(StudentService.BY_NAME));
        mixed.addAll(permits.getAll());
        System.out.println("--- Students and permit applications (" + mixed.size() + ") ---");
        for (Reportable r : mixed) {
            System.out.println("  " + r.reportLine());
        }
    }
}
