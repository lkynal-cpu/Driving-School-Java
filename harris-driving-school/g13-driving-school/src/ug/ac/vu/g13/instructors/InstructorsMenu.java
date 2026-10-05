package ug.ac.vu.g13.instructors;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.Reportable;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Submenu for the Instructors module.
 *
 * @author Member 2 (Instructors module owner)
 */
public class InstructorsMenu extends AbstractModuleMenu {
    private final InstructorService instructors;
    private final ShiftService shifts;

    public InstructorsMenu(InstructorService instructors, ShiftService shifts) {
        this.instructors = instructors;
        this.shifts = shifts;
    }

    @Override
    public String getName() {
        return "Instructors";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Register an instructor", "List all instructors", "Find an instructor by ID",
            "Update an instructor", "Remove an instructor", "Report: instructors (sorted / filtered)",
            "Add a working shift", "List shifts", "Remove a shift", "Overview of instructors and shifts"
        };
    }

    @Override
    protected void handle(int choice) {
        switch (choice) {
            case 1: register(); break;
            case 2: Menu.printList("All instructors", instructors.sorted(InstructorService.BY_NAME)); break;
            case 3: System.out.println("  " + instructors.require(InputHelper.readRequired("Instructor ID (e.g. G13-I001): ")).describe()); break;
            case 4: update(); break;
            case 5: remove(); break;
            case 6: report(); break;
            case 7: addShift(); break;
            case 8: Menu.printList("Shifts", shifts.getAll()); break;
            case 9: removeShift(); break;
            case 10: overview(); break;
            default: break;
        }
    }

    private Set<VehicleClass> askQualifications() {
        Set<VehicleClass> quals = EnumSet.noneOf(VehicleClass.class);
        for (VehicleClass vc : VehicleClass.values()) {
            if (InputHelper.readYesNo("Qualified to teach " + vc + "?")) {
                quals.add(vc);
            }
        }
        return quals;
    }

    private void register() {
        String name = InputHelper.readRequired("Full name: ");
        String phone = InputHelper.readRequired("Phone (invented number, e.g. 0700000000): ");
        int years = InputHelper.readInt("Years of experience (0-50): ", 0, 50);
        Set<VehicleClass> quals = askQualifications();
        Instructor i = instructors.register(name, phone, quals, years);
        Menu.ok("Registered " + i.getFullName() + " with ID " + i.getId());
    }

    private void update() {
        String id = InputHelper.readRequired("Instructor ID: ");
        instructors.require(id);
        int f = InputHelper.readChoice("Change: ", "Phone", "Years of experience", "Active / inactive", "Qualifications");
        if (f == 0) {
            String v = InputHelper.readRequired("New phone: ");
            instructors.update(id, i -> i.setPhone(v));
        } else if (f == 1) {
            int v = InputHelper.readInt("Years of experience: ", 0, 50);
            instructors.update(id, i -> i.setYearsExperience(v));
        } else if (f == 2) {
            boolean v = InputHelper.readYesNo("Is the instructor active?");
            instructors.update(id, i -> i.setActive(v));
        } else {
            Set<VehicleClass> v = askQualifications();
            instructors.update(id, i -> i.setQualifications(v));
        }
        Menu.ok("Instructor updated.");
    }

    private void remove() {
        String id = InputHelper.readRequired("Instructor ID to remove: ");
        Instructor i = instructors.require(id);
        if (InputHelper.readYesNo("Remove " + i.getFullName() + "?")) {
            instructors.remove(id);
            Menu.ok("Instructor removed.");
        }
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "Most experienced first", "Name (A-Z)", "Only those qualified for one class",
                "Only active instructors");
        if (how == 0) {
            Menu.printList("Instructors by experience", instructors.sorted(InstructorService.BY_EXPERIENCE_DESC));
        } else if (how == 1) {
            Menu.printList("Instructors by name", instructors.sorted(InstructorService.BY_NAME));
        } else if (how == 2) {
            VehicleClass vc = InputHelper.readEnum("Which class? ", VehicleClass.values());
            List<Instructor> list = instructors.qualifiedFor(vc);
            list.sort(InstructorService.BY_EXPERIENCE_DESC);
            Menu.printList("Instructors qualified for " + vc, list);
        } else {
            List<Instructor> list = instructors.filter(Instructor::isActive);
            list.sort(InstructorService.BY_NAME);
            Menu.printList("Active instructors", list);
        }
    }

    private void addShift() {
        Instructor i = instructors.require(InputHelper.readRequired("Instructor ID: "));
        DayOfWeek day = InputHelper.readEnum("Day of the week: ", DayOfWeek.values());
        LocalTime start = InputHelper.readTime("Start time");
        LocalTime end = InputHelper.readTime("End time");
        Shift s = shifts.addShift(i, day, start, end);
        Menu.ok("Shift " + s.getId() + " added.");
    }

    private void removeShift() {
        String id = InputHelper.readRequired("Shift ID (e.g. G13-H001): ");
        shifts.remove(id);
        Menu.ok("Shift removed.");
    }

    /** Polymorphism: one loop over instructors and shifts through Reportable. */
    private void overview() {
        List<Reportable> mixed = new ArrayList<>(instructors.sorted(InstructorService.BY_NAME));
        mixed.addAll(shifts.getAll());
        System.out.println("--- Instructors and shifts (" + mixed.size() + ") ---");
        for (Reportable r : mixed) {
            System.out.println("  " + r.reportLine());
        }
    }
}
