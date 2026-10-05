package ug.ac.vu.g13.instructors;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Manages the list of instructors.
 *
 * @author Member 2 (Instructors module owner)
 */
public class InstructorService extends BaseService<Instructor> {
    public static final Comparator<Instructor> BY_EXPERIENCE_DESC =
            Comparator.comparingInt(Instructor::getYearsExperience).reversed().thenComparing(Instructor::getFullName);
    public static final Comparator<Instructor> BY_NAME = Comparator.comparing(i -> i.getFullName().toLowerCase());

    public InstructorService() {
        super("instructors.txt", "Instructor");
        load();
    }

    @Override
    protected Instructor fromFileLine(String line) {
        String[] f = Fields.split(line, 6);
        Set<VehicleClass> quals = EnumSet.noneOf(VehicleClass.class);
        for (String token : f[3].split(",")) {
            quals.add(VehicleClass.valueOf(token.trim()));
        }
        return new Instructor(f[0], f[1], f[2], quals, Integer.parseInt(f[4]), Boolean.parseBoolean(f[5]));
    }

    public Instructor register(String name, String phone, Set<VehicleClass> quals, int years) {
        Instructor i = new Instructor(nextId('I'), name, phone, quals, years, true);
        add(i);
        return i;
    }

    public List<Instructor> qualifiedFor(VehicleClass vc) {
        return filter(i -> i.isQualifiedFor(vc));
    }
}
